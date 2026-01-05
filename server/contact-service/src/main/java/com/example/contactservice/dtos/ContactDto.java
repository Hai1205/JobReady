package com.example.contactservice.dtos;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDateTime;
import java.util.UUID;

import com.example.contactservice.entities.Contact.ContactStatus;
import lombok.Builder;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ContactDto {
    private UUID id;
    private UUID resolvedBy;
    private String name;
    private String email;
    private String plan;
    private String phone;
    private String message;
    private ContactStatus status;
    private LocalDateTime resolvedAt;
}
