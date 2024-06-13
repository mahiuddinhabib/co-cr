package com.habib.cocr;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.habib.cocr.utils.GlobalState;

public class AccountDetailActivity extends AppCompatActivity {
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private TextView emailTextView, idTextView, nameTextView, contactNoTextView;
    private Button logoutButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_detail);

        emailTextView = findViewById(R.id.emailTextView);
        idTextView = findViewById(R.id.idTextView);
        nameTextView = findViewById(R.id.nameTextView);
        contactNoTextView = findViewById(R.id.contactNoTextView);

        // Get the instance of Global State
        GlobalState globalState = GlobalState.getInstance(getApplicationContext());

        // Set the user's details
        emailTextView.setText(globalState.getCurrentUserEmail());
        idTextView.setText(globalState.getCurrentUserId());
        nameTextView.setText(globalState.getCurrentUserName());
        contactNoTextView.setText(globalState.getCurrentUserContactNo());


        logoutButton = findViewById(R.id.logoutButton);

        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(AccountDetailActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser user = mAuth.getCurrentUser();
        if (user == null) {
            Intent intent = new Intent(AccountDetailActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        }
    }
}