package com.example.cvservice.entities;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.util.UUID;

import com.example.cvservice.dtos.EducationDto;

@Entity
@Table(name = "educations")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Education {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private String school;
    private String degree;
    private String field;
    private String startDate;
    private String endDate;

    public Education(String school, String degree, String field, String startDate, String endDate) {
        this.school = school;
        this.degree = degree;
        this.field = field;
        this.startDate = startDate;
        this.endDate = endDate;
    }
    public Education(EducationDto educationDto) {
        this.school = educationDto.getSchool();
        this.degree = educationDto.getDegree();
        this.field = educationDto.getField();
        this.startDate = educationDto.getStartDate();
        this.endDate = educationDto.getEndDate();
    }
}
