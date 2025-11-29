package com.example.aiservice.entities;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "cv_templates", indexes = {
    @Index(name = "idx_category_level", columnList = "category,level"),
    @Index(name = "idx_section", columnList = "section"),
    @Index(name = "idx_rating", columnList = "rating")
})
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CVTemplateEntity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;
    
    @Column(nullable = false, length = 50)
    private String category; // tech, marketing, finance
    
    @Column(nullable = false, length = 20)
    private String level; // junior, mid, senior
    
    @Column(nullable = false, length = 50)
    private String section; // summary, experience, education, skills
    
    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;
    
    @Column(columnDefinition = "TEXT")
    private String improvedContent;
    
    @Column(nullable = false)
    private Integer rating = 3; // 1-5
    
    @ElementCollection
    @CollectionTable(name = "cv_template_keywords", joinColumns = @JoinColumn(name = "template_id"))
    @Column(name = "keyword")
    private List<String> keywords;
    
    @Column(nullable = false)
    private Boolean isActive = true;
    
    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @UpdateTimestamp
    @Column(nullable = false)
    private LocalDateTime updatedAt;
}