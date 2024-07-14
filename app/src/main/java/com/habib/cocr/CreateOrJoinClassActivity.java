package com.habib.cocr;

import static com.habib.cocr.utils.GlobalState.getCurrentUserId;
import static com.habib.cocr.utils.GlobalState.setCurrentUserClassId;
import static com.habib.cocr.utils.GlobalState.setCurrentUserDepartmentId;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Date;

public class CreateOrJoinClassActivity extends AppCompatActivity {
    private static final String TAG = "CreateOrJoinClassActivity";
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_or_join_class);

        db = FirebaseFirestore.getInstance();

        Button createClassButton = findViewById(R.id.createClassButton);
        Button joinClassButton = findViewById(R.id.joinClassButton);

        createClassButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CreateOrJoinClassActivity.this, CreateNewClassActivity.class);
                startActivity(intent);
                finish();
            }
        });

        joinClassButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showJoinClassDialog();
            }
        });
    }

    private void showJoinClassDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_join_class, null);
        builder.setView(view);

        EditText etClassCode = view.findViewById(R.id.etClassCode);
        Button btnJoinClass = view.findViewById(R.id.btnJoinClass);

        AlertDialog dialog = builder.create();

        btnJoinClass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String classCode = etClassCode.getText().toString();
                if (!classCode.isEmpty()) {
                    joinClass(classCode);
                    dialog.dismiss();
                } else {
                    etClassCode.setError("Please enter a class code");
                }
            }
        });

        dialog.show();
    }

    private void joinClass(String classCode) {
        // Fetch the class code document from Firestore
        db.collection("classCodes")
                .whereEqualTo("code", classCode)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            QuerySnapshot querySnapshot = task.getResult();
                            if (!querySnapshot.isEmpty()) {
                                // Check if the class code is valid
                                DocumentSnapshot classCodeDocument = querySnapshot.getDocuments().get(0);
                                Date expires = classCodeDocument.getDate("expires");
                                if (expires != null && expires.after(new Date())) {
                                    // The class code is valid, fetch the class document
                                    String classId = classCodeDocument.getId();
                                    fetchClassAndJoinUser(classId);
                                } else {
                                    // The class code is expired
                                    Toast.makeText(CreateOrJoinClassActivity.this, "The class code is expired.", Toast.LENGTH_SHORT).show();
                                }
                            } else {
                                // The class code does not exist
                                Toast.makeText(CreateOrJoinClassActivity.this, "The class code does not exist.", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            // Error fetching the class code document
                            Log.w(TAG, "Error getting documents.", task.getException());
                        }
                    }
                });
    }

    private void fetchClassAndJoinUser(String classId) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("classes")
                .document(classId)
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot classDocument = task.getResult();
                            if (classDocument.exists()) {
                                String departmentId = classDocument.getString("departmentId");
                                updateUserClassAndDepartment(classId, departmentId);
                            } else {
                                // The class does not exist
                                Toast.makeText(CreateOrJoinClassActivity.this, "The class does not exist.", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            // Error fetching the class document
                            Log.w(TAG, "Error getting documents.", task.getException());
                        }
                    }
                });
    }

    private void updateUserClassAndDepartment(String classId, String departmentId) {
        String currentUserId = getCurrentUserId();
        if (currentUserId != null) {
            DocumentReference userRef = db.collection("users").document(currentUserId);
            userRef.update("classId", classId, "departmentId", departmentId)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            setCurrentUserClassId(classId);
                            setCurrentUserDepartmentId(departmentId);

                            Toast.makeText(CreateOrJoinClassActivity.this, "Joined class successfully.", Toast.LENGTH_SHORT).show();

                            Intent intent = new Intent(CreateOrJoinClassActivity.this, MainActivity.class);
                            startActivity(intent);
                            finish();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.w(TAG, "Error updating user", e);
                        }
                    });
        }
    }
}