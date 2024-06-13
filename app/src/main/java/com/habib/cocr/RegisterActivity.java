package com.habib.cocr;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.habib.cocr.utils.SpinnerItem;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity {

    private static final String TAG = "RegisterActivity";
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private EditText emailField, passwordField, nameField, contactNoField;
    private Spinner institutionSpinner, departmentSpinner, roleSpinner, classSpinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        emailField = findViewById(R.id.emailField);
        passwordField = findViewById(R.id.passwordField);
        nameField = findViewById(R.id.nameField);
        contactNoField = findViewById(R.id.contactNoField);
        institutionSpinner = findViewById(R.id.institutionSpinner);
        departmentSpinner = findViewById(R.id.departmentSpinner);
        roleSpinner = findViewById(R.id.roleSpinner);
        classSpinner = findViewById(R.id.classSpinner);

        populateInstitutions();
        populateRoles();

//        institutionSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//            @Override
//            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//                String selectedInstitutionId = ((SpinnerItem)parent.getItemAtPosition(position)).getValue();
//                populateDepartments(selectedInstitutionId);
//            }
//
//            @Override
//            public void onNothingSelected(AdapterView<?> parent) {
//            }
//        });

        institutionSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                SpinnerItem selectedItem = (SpinnerItem) parent.getItemAtPosition(position);
                if (selectedItem.getName().equals("Add new institution")) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(RegisterActivity.this);
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
                            // Add other fields as necessary

                            db.collection("institutions")
                                    .add(institution)
                                    .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                        @Override
                                        public void onSuccess(DocumentReference documentReference) {
                                            // Refresh spinner items
                                            populateInstitutions();
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
                } else {
                    String selectedInstitutionId = selectedItem.getValue();
                    populateDepartments(selectedInstitutionId);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
//        departmentSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//            @Override
//            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//                String selectedDepartmentId = ((SpinnerItem) parent.getItemAtPosition(position)).getValue();
//                populateClasses(selectedDepartmentId);
//            }
//
//            @Override
//            public void onNothingSelected(AdapterView<?> parent) {
//            }
//        });

        departmentSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                SpinnerItem selectedItem = (SpinnerItem) parent.getItemAtPosition(position);
                if (selectedItem.getName().equals("Add new department")) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(RegisterActivity.this);
                    builder.setTitle("Add new department");

                    // Inflate the custom layout
                    View dialogView = getLayoutInflater().inflate(R.layout.dialog_add_department, null);
                    builder.setView(dialogView);

                    // Get the fields
                    final EditText nameField = dialogView.findViewById(R.id.nameField);
                    final EditText acronymField = dialogView.findViewById(R.id.acronymField);
                    // Add more fields as necessary

                    // Set up the buttons
                    builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            String newDepartmentName = nameField.getText().toString();
                            String newDepartmentAcronym = acronymField.getText().toString();
                            // Get other field values as necessary

                            // Add new department to Firestore
                            Map<String, Object> department = new HashMap<>();
                            department.put("name", newDepartmentName);
                            department.put("acronym", newDepartmentAcronym);
                            department.put("institutionId", ((SpinnerItem) institutionSpinner.getSelectedItem()).getValue());
                            // Add other fields as necessary

                            db.collection("departments")
                                    .add(department)
                                    .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                        @Override
                                        public void onSuccess(DocumentReference documentReference) {
                                            // Refresh spinner items
                                            populateDepartments(((SpinnerItem) institutionSpinner.getSelectedItem()).getValue());
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
                } else {
                    String selectedDepartmentId = ((SpinnerItem) parent.getItemAtPosition(position)).getValue();
                    populateClasses(selectedDepartmentId);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        roleSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedRole = ((SpinnerItem) parent.getItemAtPosition(position)).getValue();
                if (selectedRole.equals("student")) {
                    classSpinner.setVisibility(View.VISIBLE);
                } else {
                    classSpinner.setVisibility(View.GONE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        Button registerButton = findViewById(R.id.registerButton);
        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registerUser();
            }
        });
    }

    private void populateInstitutions() {
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
                            ArrayAdapter<SpinnerItem> adapter = new ArrayAdapter<>(RegisterActivity.this, android.R.layout.simple_spinner_item, institutions);
                            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                            institutionSpinner.setAdapter(adapter);
                        } else {
                            Log.w(TAG, "Error getting documents.", task.getException());
                        }
                    }
                });
    }

    private void populateDepartments(String institutionId) {
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
                            ArrayAdapter<SpinnerItem> adapter = new ArrayAdapter<>(RegisterActivity.this, android.R.layout.simple_spinner_item, departments);
                            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                            departmentSpinner.setAdapter(adapter);
                        } else {
                            Log.w(TAG, "Error getting documents.", task.getException());
                        }
                    }
                });
    }

//    private void populateClasses(String departmentId) {
//        db.collection("classes")
//                .whereEqualTo("departmentId", departmentId)
//                .get()
//                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
//                    @Override
//                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
//                        if (task.isSuccessful()) {
//                            List<SpinnerItem> classes = new ArrayList<>();
//                            classes.add(new SpinnerItem("", "Select Class"));
//                            for (QueryDocumentSnapshot document : task.getResult()) {
//                                classes.add(new SpinnerItem(document.getId(), document.getString("name")));
//                            }
//                            classes.add(new SpinnerItem("", "Add new class"));
//                            ArrayAdapter<SpinnerItem> adapter = new ArrayAdapter<>(RegisterActivity.this, android.R.layout.simple_spinner_item, classes);
//                            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//                            classSpinner.setAdapter(adapter);
//                        } else {
//                            Log.w(TAG, "Error getting documents.", task.getException());
//                        }
//                    }
//                });
//    }

    private void populateClasses(String departmentId) {
        db.collection("classes")
                .whereEqualTo("departmentId", departmentId)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            List<SpinnerItem> classes = new ArrayList<>();
                            classes.add(new SpinnerItem("", "Select Class"));
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                classes.add(new SpinnerItem(document.getId(), document.getString("name")));
                            }
                            classes.add(new SpinnerItem("", "Add new class"));
                            ArrayAdapter<SpinnerItem> adapter = new ArrayAdapter<>(RegisterActivity.this, android.R.layout.simple_spinner_item, classes);
                            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                            classSpinner.setAdapter(adapter);
                        } else {
                            Log.w(TAG, "Error getting documents.", task.getException());
                        }
                    }
                });

        classSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                SpinnerItem selectedItem = (SpinnerItem) parent.getItemAtPosition(position);
                if (selectedItem.getName().equals("Add new class")) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(RegisterActivity.this);
                    builder.setTitle("Add new class");

                    // Inflate the custom layout
                    View dialogView = getLayoutInflater().inflate(R.layout.dialog_add_class, null);
                    builder.setView(dialogView);

                    // Get the fields
                    final EditText nameField = dialogView.findViewById(R.id.nameField);
                    // Add more fields as necessary

                    // Set up the buttons
                    builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            String newClassName = nameField.getText().toString();
                            // Get other field values as necessary

                            // Add new class to Firestore
                            Map<String, Object> classMap = new HashMap<>();
                            classMap.put("name", newClassName);
                            classMap.put("departmentId", departmentId);
                            // Add other fields as necessary

                            db.collection("classes")
                                    .add(classMap)
                                    .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                        @Override
                                        public void onSuccess(DocumentReference documentReference) {
                                            // Refresh spinner items
                                            populateClasses(departmentId);
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
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    private void populateRoles() {
        List<SpinnerItem> roles = new ArrayList<>();
        roles.add(new SpinnerItem("", "Select Role"));
        roles.add(new SpinnerItem("teacher", "Teacher"));
        roles.add(new SpinnerItem("student", "Student"));
        ArrayAdapter<SpinnerItem> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, roles);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        roleSpinner.setAdapter(adapter);
    }

    private void registerUser() {
        String email = emailField.getText().toString();
        String password = passwordField.getText().toString();
        String name = nameField.getText().toString();
        String contactNo = contactNoField.getText().toString();
        String institutionId = ((SpinnerItem) institutionSpinner.getSelectedItem()).getValue();
        String departmentId = ((SpinnerItem) departmentSpinner.getSelectedItem()).getValue();
        String role = ((SpinnerItem) roleSpinner.getSelectedItem()).getValue();
        String classId = role.equals("student") ? ((SpinnerItem) classSpinner.getSelectedItem()).getValue() : null;

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            FirebaseUser user = mAuth.getCurrentUser();

                            Map<String, Object> userMap = new HashMap<>();
                            userMap.put("name", name);
                            userMap.put("email", email);
                            userMap.put("contactNo", contactNo);
//                            userMap.put("institution", institutionId);
                            userMap.put("departmentId", departmentId);
                            userMap.put("role", role);
                            if (classId != null) {
                                userMap.put("classId", classId);
                            }

                            db.collection("users").document(user.getUid())
                                    .set(userMap)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            Log.d(TAG, "DocumentSnapshot added with ID: " + user.getUid());
                                            startActivity(new Intent(RegisterActivity.this, MainActivity.class));
                                            finish();
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Log.w(TAG, "Error adding document", e);
                                            user.delete()
                                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<Void> task) {
                                                            if (task.isSuccessful()) {
                                                                Log.d(TAG, "User deleted from Firebase Authentication service");
                                                            }
                                                        }
                                                    });
                                            Toast.makeText(RegisterActivity.this, "Failed to create user. Please try again.",
                                                    Toast.LENGTH_SHORT).show();
                                        }
                                    });
                        } else {
                            Log.w(TAG, "createUserWithEmail:failure", task.getException());
                            Toast.makeText(RegisterActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}