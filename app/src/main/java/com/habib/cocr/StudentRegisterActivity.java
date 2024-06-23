package com.habib.cocr;

import static com.habib.cocr.utils.GlobalState.loadCurrentUser;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class StudentRegisterActivity extends AppCompatActivity {

    private static final String TAG = "StudentRegisterActivity";
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private EditText emailField, passwordField, nameField, contactNoField;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_register);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        emailField = findViewById(R.id.emailField);
        passwordField = findViewById(R.id.passwordField);
        nameField = findViewById(R.id.nameField);
        contactNoField = findViewById(R.id.contactNoField);


        Button registerButton = findViewById(R.id.registerButton);
        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registerUser();
            }
        });
    }
    private void registerUser() {
        String email = emailField.getText().toString();
        String password = passwordField.getText().toString();
        String name = nameField.getText().toString();
        String contactNo = contactNoField.getText().toString();

        // Check if the required fields are not empty
        if (name.isEmpty() || email.isEmpty() || password.isEmpty()) {
            Toast.makeText(StudentRegisterActivity.this, "Please fill in all the required fields.", Toast.LENGTH_LONG).show();
            return;
        }

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful() && mAuth.getCurrentUser() != null) {
                            FirebaseUser user = mAuth.getCurrentUser();

                            Map<String, Object> userMap = new HashMap<>();
                            userMap.put("name", name);
                            userMap.put("email", email);
                            userMap.put("contactNo", contactNo);
                            userMap.put("role", "student");

                            db.collection("users").document(user.getUid())
                                    .set(userMap)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            Log.d(TAG, "DocumentSnapshot added with ID: " + user.getUid());
                                            // Store user data in global state
//                                            GlobalState globalState = GlobalState.getInstance(StudentRegisterActivity.this);
                                            loadCurrentUser(user);
                                            startActivity(new Intent(StudentRegisterActivity.this, MainActivity.class));
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
                                            Toast.makeText(StudentRegisterActivity.this, "Failed to create user. Please try again.",
                                                    Toast.LENGTH_SHORT).show();
                                        }
                                    });
                        } else {
                            Log.w(TAG, "createUserWithEmail:failure", task.getException());
                            Toast.makeText(StudentRegisterActivity.this, "Authentication failed.",
                                    Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }
}