package com.example.cvservice.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.util.UUID;

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
}
