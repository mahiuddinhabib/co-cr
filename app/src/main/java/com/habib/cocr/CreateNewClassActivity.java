package com.habib.cocr;

import static com.habib.cocr.utils.GlobalState.getCurrentUserId;
import static com.habib.cocr.utils.GlobalState.setCurrentUserClassId;
import static com.habib.cocr.utils.GlobalState.setCurrentUserDepartmentId;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.habib.cocr.interfaces.OnSpinnerItemPopulatedCallback;
import com.habib.cocr.utils.RandomString;
import com.habib.cocr.utils.SpinnerItem;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CreateNewClassActivity extends AppCompatActivity {
    private static final String TAG = "CreateNewClassActivity";
    private FirebaseFirestore db;
    private EditText classNameField;
    private Spinner institutionSpinner, departmentSpinner;
    private Button createNewClassButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_new_class);

        db = FirebaseFirestore.getInstance();

        classNameField = findViewById(R.id.classNameField);
        institutionSpinner = findViewById(R.id.institutionSpinner);
        departmentSpinner = findViewById(R.id.departmentSpinner);
        createNewClassButton = findViewById(R.id.createNewClassButton);

        departmentSpinner.setEnabled(false);
        populateInstitutions(null);

        institutionSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                SpinnerItem selectedItem = (SpinnerItem) parent.getItemAtPosition(position);
                if (selectedItem.getName().equals("Add new institution")) {
                    addNewInstitution();
                } else {
                    String selectedInstitutionId = selectedItem.getValue();
                    if (selectedInstitutionId != null && !selectedInstitutionId.isEmpty()) {
                        departmentSpinner.setEnabled(true);
                        populateDepartments(selectedInstitutionId, null);
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        departmentSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                SpinnerItem selectedItem = (SpinnerItem) parent.getItemAtPosition(position);
                if (selectedItem.getName().equals("Add new department")) {
                    addNewDepartment();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        createNewClassButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createNewClass();
            }
        });
    }
    private void populateInstitutions(final OnSpinnerItemPopulatedCallback callback) {
        db.collection("institutions")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            List<SpinnerItem> institutions = new ArrayList<>();
                            institutions.add(new SpinnerItem("", "Select Institution"));
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                institutions.add(new SpinnerItem(document.getId(), document.getString("name")));
                            }
                            institutions.add(new SpinnerItem("", "Add new institution"));
                            ArrayAdapter<SpinnerItem> adapter = new ArrayAdapter<>(CreateNewClassActivity.this, android.R.layout.simple_spinner_item, institutions);
                            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                            institutionSpinner.setAdapter(adapter);

                            // Invoke the callback
                            if (callback != null) {
                                callback.onSpinnerItemPopulated(institutions);
                            }
                        } else {
                            Log.w(TAG, "Error getting documents.", task.getException());
                        }
                    }
                });
    }

    private void populateDepartments(String institutionId, final OnSpinnerItemPopulatedCallback callback) {
        db.collection("departments")
                .whereEqualTo("institutionId", institutionId)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            List<SpinnerItem> departments = new ArrayList<>();
                            departments.add(new SpinnerItem("", "Select Department"));
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                departments.add(new SpinnerItem(document.getId(), document.getString("name")));
                            }
                            departments.add(new SpinnerItem("", "Add new department"));
                            ArrayAdapter<SpinnerItem> adapter = new ArrayAdapter<>(CreateNewClassActivity.this, android.R.layout.simple_spinner_item, departments);
                            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                            departmentSpinner.setAdapter(adapter);

                            // Invoke the callback
                            if (callback != null) {
                                callback.onSpinnerItemPopulated(departments);
                            }
                        } else {
                            Log.w(TAG, "Error getting documents.", task.getException());
                        }
                    }
                });
    }

    // Add new institution
    private void addNewInstitution() {
        AlertDialog.Builder builder = new AlertDialog.Builder(CreateNewClassActivity.this);
        builder.setTitle("Add new institution");

        // Inflate the custom layout
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_add_institution, null);
        builder.setView(dialogView);

        // Get the fields
        final EditText nameField = dialogView.findViewById(R.id.nameField);
        final EditText acronymField = dialogView.findViewById(R.id.acronymField);
        final EditText locationField = dialogView.findViewById(R.id.locationField);

        // Set up the buttons
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String newInstitutionName = nameField.getText().toString();
                String newInstitutionAcronym = acronymField.getText().toString();
                String newInstitutionLocation = locationField.getText().toString();

                // Add new institution to Firestore
                Map<String, Object> institution = new HashMap<>();
                institution.put("name", newInstitutionName);
                institution.put("acronym", newInstitutionAcronym);
                institution.put("location", newInstitutionLocation);


                // Add a new document to Firestore
                db.collection("institutions")
                        .add(institution)
                        .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                            @Override
                            public void onSuccess(DocumentReference documentReference) {
                                // Refresh spinner items
                                populateInstitutions(new OnSpinnerItemPopulatedCallback() {
                                    @Override
                                    public void onSpinnerItemPopulated(List<SpinnerItem> institutions) {
                                        for (int i = 0; i < institutionSpinner.getCount(); i++) {
                                            SpinnerItem spinnerItem = (SpinnerItem) institutionSpinner.getItemAtPosition(i);
                                            if(spinnerItem.getName().equals(newInstitutionName)) {
                                                institutionSpinner.setSelection(i);
                                                break;
                                            }
                                        }
                                    }
                                });
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.w(TAG, "Error adding document", e);
                            }
                        });
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();
    }

    // Add new department
    private void addNewDepartment() {
        AlertDialog.Builder builder = new AlertDialog.Builder(CreateNewClassActivity.this);
        builder.setTitle("Add new department");

        // Inflate the custom layout
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_add_department, null);
        builder.setView(dialogView);

        // Get the fields
        final EditText nameField = dialogView.findViewById(R.id.nameField);
        final EditText acronymField = dialogView.findViewById(R.id.acronymField);

        // Set up the buttons
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String newDepartmentName = nameField.getText().toString();
                String newDepartmentAcronym = acronymField.getText().toString();

                // Add new department to Firestore
                Map<String, Object> department = new HashMap<>();
                department.put("name", newDepartmentName);
                department.put("acronym", newDepartmentAcronym);
                department.put("institutionId", ((SpinnerItem) institutionSpinner.getSelectedItem()).getValue());

                // Add a new document to Firestore
                db.collection("departments")
                        .add(department)
                        .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                            @Override
                            public void onSuccess(DocumentReference documentReference) {
                                // Refresh spinner items
                                populateDepartments(((SpinnerItem) institutionSpinner.getSelectedItem()).getValue(), new OnSpinnerItemPopulatedCallback() {
                                    @Override
                                    public void onSpinnerItemPopulated(List<SpinnerItem> departments) {
                                        for (int i = 0; i < departmentSpinner.getCount(); i++) {
                                            SpinnerItem spinnerItem = (SpinnerItem) departmentSpinner.getItemAtPosition(i);
                                            if(spinnerItem.getName().equals(newDepartmentName)) {
                                                departmentSpinner.setSelection(i);
                                                break;
                                            }
                                        }
                                    }
                                });
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.w(TAG, "Error adding document", e);
                            }
                        });
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();
    }

    private void createNewClass() {
        String className = classNameField.getText().toString();
        String institutionId = ((SpinnerItem) institutionSpinner.getSelectedItem()).getValue();
        String departmentId = ((SpinnerItem) departmentSpinner.getSelectedItem()).getValue();

        if(className.isEmpty() || institutionId.isEmpty() || departmentId.isEmpty()) {
            return;
        }

        // Add new class to Firestore
        Map<String, Object> newClass = new HashMap<>();
        newClass.put("name", className);
        newClass.put("institutionId", institutionId);
        newClass.put("departmentId", departmentId);

        db.collection("classes")
                .add(newClass)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        createClassCode(documentReference.getId());
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error adding document", e);
                    }
                });
    }

    private void createClassCode(String classId) {
        String code = RandomString.generateRandomString(6);
        checkIfCodeExists(code, classId);
    }

    private void checkIfCodeExists(String code, String classId) {
        db.collection("classCodes")
                .whereEqualTo("code", code)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            if (task.getResult().isEmpty()) {
                                addClassCode(code, classId, ((SpinnerItem) departmentSpinner.getSelectedItem()).getValue());
                            } else {
                                createClassCode(classId);
                            }
                        } else {
                            Log.w(TAG, "Error getting documents.", task.getException());
                            deleteClass(classId);
                        }
                    }
                });
    }

    private void addClassCode(String code, String classId, String departmentId) {
        Map<String, Object> classCode = new HashMap<>();
        classCode.put("code", code);
        classCode.put("expires", new Date(System.currentTimeMillis() + 3 * 24 * 60 * 60 * 1000)); // 3 days from now

        db.collection("classCodes")
                .document(classId)
                .set(classCode)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        updateUserClassAndDepartment(classId, departmentId, code);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error adding document", e);
                        deleteClass(classId);
                    }
                });
    }

    private void updateUserClassAndDepartment(String classId, String departmentId , String code) {
        String currentUserId = getCurrentUserId();
        if (currentUserId != null) {
            DocumentReference userRef = db.collection("users").document(currentUserId);
            userRef.update("classId", classId, "departmentId", departmentId)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            setCurrentUserClassId(classId);
                            setCurrentUserDepartmentId(departmentId);

                            showClassCodeBottomSheet(code);

                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.w(TAG, "Error updating user", e);
                            deleteClass(classId);
                        }
                    });
        }
    }

    private void deleteClass(String classId) {
        db.collection("classes")
                .document(classId)
                .delete()
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error deleting document", e);
                    }
                });
    }

    private void showClassCodeBottomSheet(String classCode) {
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(CreateNewClassActivity.this);
        View view = getLayoutInflater().inflate(R.layout.dialog_class_code, null);
        bottomSheetDialog.setContentView(view);

        TextView tvClassCode = view.findViewById(R.id.tvClassCode);
        Button btnCopyCode = view.findViewById(R.id.btnCopyCode);

        tvClassCode.setText(classCode);

        btnCopyCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText("classCode", classCode);
                clipboard.setPrimaryClip(clip);

                Toast.makeText(CreateNewClassActivity.this, "Code copied to clipboard", Toast.LENGTH_SHORT).show();

                bottomSheetDialog.dismiss();
            }
        });

        bottomSheetDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                startActivity(new Intent(CreateNewClassActivity.this, MainActivity.class));
                finish();
            }
        });

        bottomSheetDialog.show();
    }
}

