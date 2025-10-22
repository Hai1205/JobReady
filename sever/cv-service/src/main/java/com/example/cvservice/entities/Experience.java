package com.example.cvservice.entities;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.util.UUID;

import com.example.cvservice.dtos.ExperienceDto;

@Entity
@Table(name = "experiences")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Experience {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private String company;
    private String position;
    private String startDate;
    private String endDate;

    @Column(columnDefinition = "TEXT")
    private String description;

    public Experience(String company, String position, String startDate, String endDate, String description) {
        this.company = company;
        this.position = position;
        this.startDate = startDate;
        this.endDate = endDate;
        this.description = description;
    }
    
    public Experience(ExperienceDto experienceDto) {
        this.company = experienceDto.getCompany();
        this.position = experienceDto.getPosition();
        this.startDate = experienceDto.getStartDate();
        this.endDate = experienceDto.getEndDate();
        this.description = experienceDto.getDescription();
    }
}
