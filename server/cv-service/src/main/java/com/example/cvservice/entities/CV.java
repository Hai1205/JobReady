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

    public CV(UUID userId, String title){
        this.userId = userId;
        this.title = title;
    }

    public CV(UUID userId, String title, List<String> skills, String privacy, String color, String template) {
        this.privacy = CVPrivacy.valueOf(privacy);
        this.title = title;
        this.userId = userId;
        this.skills = skills;
        this.color = color;
        this.template = template;
    }

    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "cv_id")
    private PersonalInfo personalInfo;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "cv_id")
    private List<Experience> experiences = new ArrayList<>();

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "cv_id")
    private List<Education> educations = new ArrayList<>();

    @ElementCollection
    private List<String> skills = new ArrayList<>();

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CVPrivacy privacy = CVPrivacy.PRIVATE;

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

    public enum CVPrivacy {
        PUBLIC,
        PRIVATE
    }
}
