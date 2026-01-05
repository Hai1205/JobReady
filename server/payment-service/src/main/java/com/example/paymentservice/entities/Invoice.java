package com.example.paymentservice.entities;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Builder;
import java.util.UUID;

@Entity
@Table(name = "invoices")
@Data
@NoArgsConstructor // Empty constructor for MapStruct
public class Invoice {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private UUID userId;
    
    private String planTitle;
    private Integer amount;
    private String currency;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private InvoiceStatus status;
    private String paymentMethod;
    private String transactionId;
    private String billingDate;
    private String periodStart;
    private String periodEnd;
    private String description;
    private String downloadUrl;

    // Full constructor for MapStruct
    @Builder
    public Invoice(UUID id, UUID userId, String planTitle, Integer amount, String currency, InvoiceStatus status, String paymentMethod, String transactionId, String billingDate, String periodStart, String periodEnd, String description, String downloadUrl) {
        this.id = id;
        this.userId = userId;
        this.planTitle = planTitle;
        this.amount = amount;
        this.currency = currency;
        this.status = status;
        this.paymentMethod = paymentMethod;
        this.transactionId = transactionId;
        this.billingDate = billingDate;
        this.periodStart = periodStart;
        this.periodEnd = periodEnd;
        this.description = description;
        this.downloadUrl = downloadUrl;
    }

    public enum InvoiceStatus {
        paid,
        pending,
        failed,
        refunded
    }

    public enum InvoiceRole {
        ADMIN,
        USER
    }
}
