package com.example.contactservice.mappers;

import com.example.contactservice.dtos.ContactDto;
import com.example.contactservice.entities.Contact;
import com.example.contactservice.entities.Contact.ContactStatus;

import org.springframework.stereotype.Component;

@Component
public class ContactMapper {

    /**
     * Maps Contact entity to ContactDto
     */
    public ContactDto toDto(Contact contact) {
        if (contact == null) {
            return null;
        }

        ContactDto dto = new ContactDto();
        dto.setId(contact.getId());
        dto.setName(contact.getName());
        dto.setEmail(contact.getEmail());
        dto.setPlan(contact.getPlan());
        dto.setPhone(contact.getPhone());
        dto.setMessage(contact.getMessage());
        dto.setStatus(contact.getStatus());
        dto.setResolvedBy(contact.getResolvedBy());
        dto.setResolvedAt(contact.getResolvedAt());

        return dto;
    }

    /**
     * Maps ContactDto to Contact entity
     */
    public Contact toEntity(ContactDto dto) {
        if (dto == null) {
            return null;
        }

        Contact contact = new Contact();
        contact.setId(dto.getId()); // Set ID for updates
        contact.setName(dto.getName());
        contact.setEmail(dto.getEmail());
        contact.setPlan(dto.getPlan());
        contact.setPhone(dto.getPhone());
        contact.setMessage(dto.getMessage());
        contact.setResolvedBy(dto.getResolvedBy());
        contact.setResolvedAt(dto.getResolvedAt());

        if (dto.getStatus() != null) {
            contact.setStatus(dto.getStatus());
        }
        return contact;
    }
}
