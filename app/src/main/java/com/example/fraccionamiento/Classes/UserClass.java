package com.example.fraccionamiento.Classes;

import com.google.firebase.database.Exclude;

import java.util.HashMap;
import java.util.Map;


// Clase para obtener los datos del usuario

public class UserClass {
    private String name;
    private String email;
    private String urlImg;
    private String lastName;
    private String role;
    private String key;
    private Boolean debt;
    private String deptNum;

    public UserClass() {
    }

    public UserClass(String name, String email, String lastName, String role, String urlImg) {
        this.name = name;
        this.email = email;
        this.lastName = lastName;
        this.role = role;
        this.urlImg = urlImg;
    }

    public UserClass(String name, String email, String lastName, String role, String urlImg, Boolean dept) {
        this.name = name;
        this.email = email;
        this.lastName = lastName;
        this.role = role;
        this.urlImg = urlImg;
        this.debt = dept;
    }

    public UserClass(HashMap<String, String> map) {
        this.name = map.get("name");
        this.email = map.get("email");
        this.lastName = map.get("lastName");
        this.role = map.get("role");
        this.urlImg = map.get("urlImg");
    }



    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
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

    public String getUrlImg() {
        return urlImg;
    }

    public void setUrlImg(String urlImg) {
        this.urlImg = urlImg;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public Boolean getDebt() {
        return debt;
    }

    public void setDebt(Boolean debt) {
        this.debt = debt;
    }

    public String getDeptNum() {
        return deptNum;
    }

    public void setDeptNum(String deptNum) {
        this.deptNum = deptNum;
    }

    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("urlImg", urlImg);
        result.put("name", name);
        result.put("lastName", lastName);
        result.put("email", email);
        result.put("role", role);
        result.put("debt", debt);
        return result;
    }
}