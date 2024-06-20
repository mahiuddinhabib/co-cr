package com.habib.cocr.utils;

import android.content.Context;
import android.content.SharedPreferences;
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
    private final SharedPreferences sharedPreferences;

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
        this.sharedPreferences = context.getSharedPreferences("GlobalStatePrefs", Context.MODE_PRIVATE);
    }

    // Getters and Setters
    public static synchronized GlobalState getInstance(Context context) {
        if (globalState == null) {
            globalState = new GlobalState(context);
        }
        return globalState;
    }

    public String getCurrentUserId() {
        if (currentUserId == null) {
            currentUserId = sharedPreferences.getString("currentUserId", null);
        }
        return currentUserId;
    }

    public void setCurrentUserId(String currentUserId) {
        this.currentUserId = currentUserId;
        sharedPreferences.edit().putString("currentUserId", currentUserId).apply();
    }

    public String getCurrentUserName() {
        if (currentUserName == null) {
            currentUserName = sharedPreferences.getString("currentUserName", null);
        }
        return currentUserName;
    }

    public void setCurrentUserName(String currentUserName) {
        this.currentUserName = currentUserName;
        sharedPreferences.edit().putString("currentUserName", currentUserName).apply();
    }

    public String getCurrentUserEmail() {
        if (currentUserEmail == null) {
            currentUserEmail = sharedPreferences.getString("currentUserEmail", null);
        }
        return currentUserEmail;
    }

    public void setCurrentUserEmail(String currentUserEmail) {
        this.currentUserEmail = currentUserEmail;
        sharedPreferences.edit().putString("currentUserEmail", currentUserEmail).apply();
    }

    public String getCurrentUserContactNo() {
        if (currentUserContactNo == null) {
            currentUserContactNo = sharedPreferences.getString("currentUserContactNo", null);
        }
        return currentUserContactNo;
    }

    public void setCurrentUserContactNo(String currentUserContactNo) {
        this.currentUserContactNo = currentUserContactNo;
        sharedPreferences.edit().putString("currentUserContactNo", currentUserContactNo).apply();
    }

    public String getCurrentUserRole() {
        if (currentUserRole == null) {
            currentUserRole = sharedPreferences.getString("currentUserRole", null);
        }
        return currentUserRole;
    }

    public void setCurrentUserRole(String currentUserRole) {
        this.currentUserRole = currentUserRole;
        sharedPreferences.edit().putString("currentUserRole", currentUserRole).apply();
    }

    public String getCurrentUserDepartmentId() {
        if (currentUserDepartmentId == null) {
            currentUserDepartmentId = sharedPreferences.getString("currentUserDepartmentId", null);
        }
        return currentUserDepartmentId;
    }

    public void setCurrentUserDepartmentId(String currentUserDepartmentId) {
        this.currentUserDepartmentId = currentUserDepartmentId;
        sharedPreferences.edit().putString("currentUserDepartmentId", currentUserDepartmentId).apply();
    }

    public String getCurrentUserClassId() {
        if (currentUserClassId == null) {
            currentUserClassId = sharedPreferences.getString("currentUserClassId", null);
        }
        return currentUserClassId;
    }

    public void setCurrentUserClassId(String currentUserClassId) {
        this.currentUserClassId = currentUserClassId;
        sharedPreferences.edit().putString("currentUserClassId", currentUserClassId).apply();
    }

    public Uri getCurrentUserProfileImg() {
        if (currentUserProfileImg == null) {
            String uriString = sharedPreferences.getString("currentUserProfileImg", null);
            if (uriString != null) {
                currentUserProfileImg = Uri.parse(uriString);
            }
        }
        return currentUserProfileImg;
    }

    public void setCurrentUserProfileImg(Uri currentUserProfileImg) {
        this.currentUserProfileImg = currentUserProfileImg;
        sharedPreferences.edit().putString("currentUserProfileImg", currentUserProfileImg.toString()).apply();
    }

    public void clearCurrentUser() {
        sharedPreferences.edit().clear().apply();
        currentUserClassId = null;
        currentUserContactNo = null;
        currentUserDepartmentId = null;
        currentUserEmail = null;
        currentUserId = null;
        currentUserName = null;
        currentUserProfileImg = null;
        currentUserRole = null;
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
