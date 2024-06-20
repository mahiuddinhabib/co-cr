package com.habib.cocr;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class RegisterRoleSelectionActivity extends AppCompatActivity {
    private Button teacherButton, studentButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_role_selection);

        teacherButton = findViewById(R.id.teacherButton);
        studentButton = findViewById(R.id.studentButton);

        teacherButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RegisterRoleSelectionActivity.this, StudentRegisterActivity.class);
                startActivity(intent);
            }
        });

        studentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RegisterRoleSelectionActivity.this, StudentRegisterActivity.class);
                startActivity(intent);
            }
        });
    }
}