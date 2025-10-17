package com.example.cvservice.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.util.UUID;

@Entity
@Table(name = "personal-infos")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PersonalInfo {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private String fullname;
    private String email;
    private String phone;
    private String location;
    private String summary;
    private String avatarUrl;
    private String avatarPublicId;

    public PersonalInfo(String fullname, String email, String phone, String location, String summary) {
        this.fullname = fullname;
        this.email = email;
        this.phone = phone;
        this.location = location;
        this.summary = summary;
    }
}
