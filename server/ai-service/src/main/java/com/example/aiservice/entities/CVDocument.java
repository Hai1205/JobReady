package com.example.aiservice.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "cv_documents")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CVDocument {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 1000)
    private String content;

    @Column(columnDefinition = "text")
    private String embedding;

    private String title;

    private String userId;

    private String metadata;
}