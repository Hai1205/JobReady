package com.example.cvservice.entity;

import jakarta.persistence.Embeddable;

@Embeddable
public class PersonalInfo {
    private String fullname;
    private String email;
    private String phone;
    private String location;
    private String summary;

    public PersonalInfo() {
    }

    public PersonalInfo(String fullname, String email, String phone, String location, String summary) {
        this.fullname = fullname;
        this.email = email;
        this.phone = phone;
        this.location = location;
        this.summary = summary;
    }

    public String getFullname() {
        return fullname;
    }

    public void setFullname(String fullname) {
        this.fullname = fullname;
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
