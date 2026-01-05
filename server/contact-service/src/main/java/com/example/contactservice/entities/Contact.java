package com.example.contactservice.entities;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Builder;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "contacts")
@Data
@NoArgsConstructor // Empty constructor for MapStruct
@EntityListeners(AuditingEntityListener.class)
public class Contact {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private UUID resolvedBy;

    private String name;
    private String email;
    private String plan;
    private String phone;
    private String message;
    private LocalDateTime resolvedAt;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ContactStatus status = ContactStatus.pending;

        // Constructor with all fields for MapStruct
    @Builder
    public Contact(UUID id, String name, String email, String plan,
            String phone, String message, ContactStatus status, UUID resolvedBy,
            LocalDateTime resolvedAt) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.plan = plan;
        this.phone = phone;
        this.message = message;
        this.status = status;
        this.resolvedBy = resolvedBy;
        this.resolvedAt = resolvedAt;
    }

    // Basic constructor
    public Contact(UUID id, String name, String email, String plan,
            String phone, String message) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.plan = plan;
        this.phone = phone;
        this.message = message;
    }

    public enum ContactStatus {
        resolved,
        pending
    }
}
