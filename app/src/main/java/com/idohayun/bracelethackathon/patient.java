package com.idohayun.bracelethackathon;

import java.util.List;

public class Patient {
    private String fullName, id, phone;
    private List<String> Diseases;

    public Patient(String fullName, String id, String phone) {
        this.fullName = fullName;
        this.id = id;
        this.phone = phone;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public List<String> getDiseases() {
        return Diseases;
    }

    public void setDiseases(List diseases) {
        Diseases = diseases;
    }
}
