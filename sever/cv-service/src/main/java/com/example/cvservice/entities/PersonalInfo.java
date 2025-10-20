package com.example.cvservice.entities;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.util.UUID;

import com.example.cvservice.dtos.PersonalInfoDto;

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
    private String avatarUrl;
    private String avatarPublicId;

    @Column(length = 2000)
    private String summary;
    
    public PersonalInfo(String fullname, String email, String phone, String location, String summary) {
        this.fullname = fullname;
        this.email = email;
        this.phone = phone;
        this.location = location;
        this.summary = summary;
    }
    
    public PersonalInfo(PersonalInfoDto personalInfoDto) {
        this.fullname = personalInfoDto.getFullname();
        this.email = personalInfoDto.getEmail();
        this.phone = personalInfoDto.getPhone();
        this.location = personalInfoDto.getLocation();
        this.summary = personalInfoDto.getSummary();
    }
}
