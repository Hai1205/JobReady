package com.example.cvservice.entities;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "cvs")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CV {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private String title;

    private UUID userId;

    public CV(UUID userId, String title) {
        this.userId = userId;
        this.title = title;
        this.isVisibility = false;
        this.color = "#3498db";
        this.template = "modern";
    }

    public CV(UUID userId, String title, List<String> skills, Boolean isVisibility, String color, String template) {
        this.isVisibility = isVisibility;
        this.title = title;
        this.userId = userId;
        this.skills = skills;
        this.color = color;
        this.template = template;
    }

    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    private PersonalInfo personalInfo;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Experience> experiences = new ArrayList<>();

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Education> educations = new ArrayList<>();

    @ElementCollection
    private List<String> skills = new ArrayList<>();

    @Column(nullable = false)
    private Boolean isVisibility = false;

    @Column(nullable = false)
    private String color = "#3498db"; // Default blue color

    @Column(nullable = false)
    private String template = "modern"; // Default modern template

    private Instant createdAt;
    private Instant updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = Instant.now();
        updatedAt = createdAt;
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = Instant.now();
    }
}
