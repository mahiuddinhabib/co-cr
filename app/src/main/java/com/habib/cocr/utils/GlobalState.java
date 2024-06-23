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
    private static SharedPreferences sharedPreferences = null;

    // Global states
    private static String currentUserId;
    private static String currentUserName;
    private static String currentUserEmail;
    private static String currentUserContactNo;
    private static String currentUserRole;
    private static String currentUserDepartmentId;
    private static String currentUserClassId;
    private static Uri currentUserProfileImg;

    // Private constructor
    private GlobalState(Context context) {
        this.applicationContext = context.getApplicationContext();
        sharedPreferences = context.getSharedPreferences("GlobalStatePrefs", Context.MODE_PRIVATE);
    }

    // Getters and Setters
    public static synchronized GlobalState getInstance(Context context) {
        if (globalState == null) {
            globalState = new GlobalState(context);
        }
        return globalState;
    }

    public static String getCurrentUserId() {
        if (currentUserId == null) {
            currentUserId = sharedPreferences.getString("currentUserId", null);
        }
        return currentUserId;
    }

    public static void setCurrentUserId(String currentUserId) {
        GlobalState.currentUserId = currentUserId;
        sharedPreferences.edit().putString("currentUserId", currentUserId).apply();
    }

    public static String getCurrentUserName() {
        if (currentUserName == null) {
            currentUserName = sharedPreferences.getString("currentUserName", null);
        }
        return currentUserName;
    }

    public static void setCurrentUserName(String currentUserName) {
        GlobalState.currentUserName = currentUserName;
        sharedPreferences.edit().putString("currentUserName", currentUserName).apply();
    }

    public static String getCurrentUserEmail() {
        if (currentUserEmail == null) {
            currentUserEmail = sharedPreferences.getString("currentUserEmail", null);
        }
        return currentUserEmail;
    }

    public static void setCurrentUserEmail(String currentUserEmail) {
        GlobalState.currentUserEmail = currentUserEmail;
        sharedPreferences.edit().putString("currentUserEmail", currentUserEmail).apply();
    }

    public static String getCurrentUserContactNo() {
        if (currentUserContactNo == null) {
            currentUserContactNo = sharedPreferences.getString("currentUserContactNo", null);
        }
        return currentUserContactNo;
    }

    public static void setCurrentUserContactNo(String currentUserContactNo) {
        GlobalState.currentUserContactNo = currentUserContactNo;
        sharedPreferences.edit().putString("currentUserContactNo", currentUserContactNo).apply();
    }

    public static String getCurrentUserRole() {
        if (currentUserRole == null) {
            currentUserRole = sharedPreferences.getString("currentUserRole", null);
        }
        return currentUserRole;
    }

    public static void setCurrentUserRole(String currentUserRole) {
        GlobalState.currentUserRole = currentUserRole;
        sharedPreferences.edit().putString("currentUserRole", currentUserRole).apply();
    }

    public static String getCurrentUserDepartmentId() {
        if (currentUserDepartmentId == null) {
            currentUserDepartmentId = sharedPreferences.getString("currentUserDepartmentId", null);
        }
        return currentUserDepartmentId;
    }

    public static void setCurrentUserDepartmentId(String currentUserDepartmentId) {
        GlobalState.currentUserDepartmentId = currentUserDepartmentId;
        sharedPreferences.edit().putString("currentUserDepartmentId", currentUserDepartmentId).apply();
    }

    public static String getCurrentUserClassId() {
        if (currentUserClassId == null) {
            currentUserClassId = sharedPreferences.getString("currentUserClassId", null);
        }
        return currentUserClassId;
    }

    public static void setCurrentUserClassId(String currentUserClassId) {
        GlobalState.currentUserClassId = currentUserClassId;
        sharedPreferences.edit().putString("currentUserClassId", currentUserClassId).apply();
    }

    public static Uri getCurrentUserProfileImg() {
        if (currentUserProfileImg == null) {
            String uriString = sharedPreferences.getString("currentUserProfileImg", null);
            if (uriString != null) {
                currentUserProfileImg = Uri.parse(uriString);
            }
        }
        return currentUserProfileImg;
    }

    public static void setCurrentUserProfileImg(Uri currentUserProfileImg) {
        GlobalState.currentUserProfileImg = currentUserProfileImg;
        sharedPreferences.edit().putString("currentUserProfileImg", currentUserProfileImg.toString()).apply();
    }

    public static void clearCurrentUser() {
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

    public static void loadCurrentUser(FirebaseUser user) {
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
                                if(user.getPhotoUrl() != null) {
                                    setCurrentUserProfileImg(user.getPhotoUrl());
                                }
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
