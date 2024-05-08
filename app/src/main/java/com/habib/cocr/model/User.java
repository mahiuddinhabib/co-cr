package com.habib.cocr.model;

public class User {
    private String userId;
    private String name;
    private String email;
    private String role;
    private String profileImg;
    private String contactNo;
    private String departmentId;
    private String classId;

    // Constructor
    public User(String userId, String name, String email, String role, String profileImg, String contactNo, String departmentId, String classId) {
        this.userId = userId;
        this.name = name;
        this.email = email;
        this.role = role;
        this.profileImg = profileImg;
        this.contactNo = contactNo;
        this.departmentId = departmentId;
        this.classId = classId;
    }

    // Getters and Setters
    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getProfileImg() {
        return profileImg;
    }

    public void setProfileImg(String profileImg) {
        this.profileImg = profileImg;
    }

    public String getContactNo() {
        return contactNo;
    }

    public void setContactNo(String contactNo) {
        this.contactNo = contactNo;
    }

    public String getDepartmentId() {
        return departmentId;
    }

    public void setDepartmentId(String departmentId) {
        this.departmentId = departmentId;
    }

    public String getClassId() {
        return classId;
    }

    public void setClassId(String classId) {
        this.classId = classId;
    }
}
