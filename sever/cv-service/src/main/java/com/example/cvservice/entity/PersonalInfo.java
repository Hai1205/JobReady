package com.example.cvservice.entity;

import jakarta.persistence.Embeddable;

@Embeddable
public class PersonalInfo {
    private String fullName;
    private String email;
    private String phone;
    private String location;
    private String summary;

    public PersonalInfo() {
    }

    public PersonalInfo(String fullName, String email, String phone, String location, String summary) {
        this.fullName = fullName;
        this.email = email;
        this.phone = phone;
        this.location = location;
        this.summary = summary;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }
}
