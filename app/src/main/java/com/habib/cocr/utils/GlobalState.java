package com.habib.cocr.utils;

import android.content.Context;
import android.net.Uri;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

// Singleton class to store global states
public class GlobalState {
    private static GlobalState globalState = null;
    private final Context applicationContext;

    // Global states
    private String currentUserId;
    private String currentUserName;
    private String currentUserEmail;
    private String currentUserContactNo;
    private String currentUserRole;
    private String currentUserDepartmentId;
    private String currentUserClassId;
    private Uri currentUserProfileImg;

    // Private constructor
    private GlobalState(Context context) {
        this.applicationContext = context.getApplicationContext();
    }

    // Getters and Setters
    public static synchronized GlobalState getInstance(Context context) {
        if (globalState == null) {
            globalState = new GlobalState(context);
        }
        return globalState;
    }

    public String getCurrentUserId() {
        return currentUserId;
    }

    public void setCurrentUserId(String currentUserId) {
        this.currentUserId = currentUserId;
    }

    public String getCurrentUserName() {
        return currentUserName;
    }

    public void setCurrentUserName(String currentUserName) {
        this.currentUserName = currentUserName;
    }

    public String getCurrentUserEmail() {
        return currentUserEmail;
    }

    public void setCurrentUserEmail(String currentUserEmail) {
        this.currentUserEmail = currentUserEmail;
    }

    public String getCurrentUserContactNo() {
        return currentUserContactNo;
    }

    public void setCurrentUserContactNo(String currentUserContactNo) {
        this.currentUserContactNo = currentUserContactNo;
    }

    public String getCurrentUserRole() {
        return currentUserRole;
    }

    public void setCurrentUserRole(String currentUserRole) {
        this.currentUserRole = currentUserRole;
    }

    public String getCurrentUserDepartmentId() {
        return currentUserDepartmentId;
    }

    public void setCurrentUserDepartmentId(String currentUserDepartmentId) {
        this.currentUserDepartmentId = currentUserDepartmentId;
    }

    public String getCurrentUserClassId() {
        return currentUserClassId;
    }

    public void setCurrentUserClassId(String currentUserClassId) {
        this.currentUserClassId = currentUserClassId;
    }

    public Uri getCurrentUserProfileImg() {
        return currentUserProfileImg;
    }

    public void setCurrentUserProfileImg(Uri currentUserProfileImg) {
        this.currentUserProfileImg = currentUserProfileImg;
    }

    public Context getApplicationContext() {
        return applicationContext;
    }

    public void clear() {
        currentUserId = null;
        currentUserName = null;
        currentUserEmail = null;
        currentUserContactNo = null;
        currentUserRole = null;
        currentUserDepartmentId = null;
        currentUserClassId = null;
        currentUserProfileImg = null;
    }

    public void loadCurrentUser(FirebaseUser user) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("users").document(user.getUid())
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document.exists()) {
                                setCurrentUserId(user.getUid());
                                setCurrentUserName(document.getString("name"));
                                setCurrentUserEmail(document.getString("email"));
                                setCurrentUserContactNo(document.getString("contactNo"));
                                setCurrentUserRole(document.getString("role"));
                                setCurrentUserDepartmentId(document.getString("departmentId"));
                                if(getCurrentUserRole().equals("student")) {
                                    setCurrentUserClassId(document.getString("classId"));
                                }
                                setCurrentUserProfileImg(user.getPhotoUrl());
                                // Add other fields as needed
                            } else {
                                Log.d("GlobalState", "No such document");
                            }
                        } else {
                            Log.d("GlobalState", "get failed with ", task.getException());
                        }
                    }
                });
    }

}
