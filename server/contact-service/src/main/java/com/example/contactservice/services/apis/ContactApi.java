package com.example.contactservice.services.apis;

import com.example.contactservice.dtos.ContactDto;
import com.example.contactservice.dtos.requests.SubmitContactRequest;
import com.example.contactservice.dtos.response.Response;
import com.example.contactservice.entities.*;
import com.example.contactservice.entities.Contact.ContactStatus;
import com.example.contactservice.exceptions.OurException;
import com.example.contactservice.mappers.ContactMapper;
import com.example.contactservice.repositories.*;
import com.example.securitycommon.utils.SecurityUtils;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class ContactApi extends BaseApi {

    private final SimpleContactRepository simpleContactRepository;
    private final ContactQueryRepository contactQueryRepository;
    private final ContactCommandRepository contactCommandRepository;
    private final ContactMapper contactMapper;
    private final ObjectMapper objectMapper;

    public ContactApi(
            SimpleContactRepository simpleContactRepository,
            ContactQueryRepository contactQueryRepository,
            ContactCommandRepository contactCommandRepository,
            ContactMapper contactMapper) {
        this.simpleContactRepository = simpleContactRepository;
        this.contactQueryRepository = contactQueryRepository;
        this.contactCommandRepository = contactCommandRepository;
        this.contactMapper = contactMapper;
        this.objectMapper = new ObjectMapper();
    }

    /**
     * Submit a new contact form
     */
    public Response submitContact(String dataJson) {
        logger.info("Processing new contact submission");
        Response response = new Response();

        try {
            SubmitContactRequest request = objectMapper.readValue(dataJson, SubmitContactRequest.class);
            
            // Validate required fields
            if (request.getName() == null || request.getName().isEmpty()) {
                throw new OurException("Name is required", 400);
            }
            if (request.getEmail() == null || request.getEmail().isEmpty()) {
                throw new OurException("Email is required", 400);
            }
            if (request.getMessage() == null || request.getMessage().isEmpty()) {
                throw new OurException("Message is required", 400);
            }

            // Create new contact
            UUID contactId = UUID.randomUUID();
            LocalDateTime now = LocalDateTime.now();
            
            int inserted = contactCommandRepository.insertContact(
                    contactId,
                    request.getName(),
                    request.getEmail(),
                    request.getPlan(),
                    request.getPhone(),
                    request.getMessage(),
                    ContactStatus.pending.name(),
                    now,
                    now);

            if (inserted == 0) {
                throw new OurException("Failed to create contact", 500);
            }

            // Fetch created contact to return
            Contact savedContact = contactQueryRepository.findContactById(contactId)
                    .orElseThrow(() -> new OurException("Failed to create contact", 500));
            logger.info("Contact submitted successfully with ID: {}", savedContact.getId());

            response.setStatusCode(201);
            response.setMessage("Contact submitted successfully");
            response.setContact(contactMapper.toDto(savedContact));

            return response;
        } catch (OurException e) {
            logger.error("Error in submitContact: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            logger.error("Unexpected error in submitContact: {}", e.getMessage(), e);
            throw new OurException("Failed to submit contact", 500);
        }
    }

    /**
     * Get all contacts with pagination
     */
    public Response getAllContacts() {
        logger.info("Fetching all contacts");
        Response response = new Response();

        try {
            Pageable pageable = PageRequest.of(0, 100, Sort.by("id").descending());
            Page<Contact> contactsPage = contactQueryRepository.findAllContacts(pageable);

            List<ContactDto> contactDtos = contactsPage.getContent().stream()
                    .map(contactMapper::toDto)
                    .collect(Collectors.toList());

            response.setStatusCode(200);
            response.setMessage("Contacts retrieved successfully");
            response.setContacts(contactDtos);

            return response;
        } catch (Exception e) {
            logger.error("Unexpected error in getAllContacts: {}", e.getMessage(), e);
            throw new OurException("Failed to retrieve contacts", 500);
        }
    }

    /**
     * Get contact by ID
     */
    public Response getContactById(UUID contactId) {
        logger.info("Fetching contact with ID: {}", contactId);
        Response response = new Response();

        try {
            Contact contact = contactQueryRepository.findContactById(contactId)
                    .orElseThrow(() -> new OurException("Contact not found", 404));

            response.setStatusCode(200);
            response.setMessage("Contact retrieved successfully");
            response.setContact(contactMapper.toDto(contact));

            return response;
        } catch (OurException e) {
            logger.error("Error in getContactById: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            logger.error("Unexpected error in getContactById: {}", e.getMessage(), e);
            throw new OurException("Failed to retrieve contact", 500);
        }
    }

    /**
     * Resolve a contact (mark as resolved)
     */
    public Response resolveContact(UUID contactId) {
        logger.info("Resolving contact with ID: {}", contactId);
        Response response = new Response();

        try {
            // Verify contact exists
            if (!simpleContactRepository.existsById(contactId)) {
                throw new OurException("Contact not found", 404);
            }

            // Get current authenticated user ID from security context
            UUID resolvedBy = SecurityUtils.getCurrentUserId();
            LocalDateTime resolvedAt = LocalDateTime.now();

            // Update contact status with resolved fields
            int updated = contactCommandRepository.updateResolvedFields(
                    contactId, 
                    ContactStatus.resolved,
                    resolvedBy,
                    resolvedAt);
            
            if (updated == 0) {
                throw new OurException("Failed to update contact status", 500);
            }

            // Fetch updated contact
            Contact updatedContact = contactQueryRepository.findContactById(contactId)
                    .orElseThrow(() -> new OurException("Contact not found after update", 500));

            logger.info("Contact {} resolved successfully by user {}", contactId, resolvedBy);

            response.setStatusCode(200);
            response.setMessage("Contact resolved successfully");
            response.setContact(contactMapper.toDto(updatedContact));

            return response;
        } catch (OurException e) {
            logger.error("Error in resolveContact: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            logger.error("Unexpected error in resolveContact: {}", e.getMessage(), e);
            throw new OurException("Failed to resolve contact", 500);
        }
    }

    /**
     * Delete a contact
     */
    public Response deleteContact(UUID contactId) {
        logger.info("Deleting contact with ID: {}", contactId);
        Response response = new Response();

        try {
            // Verify contact exists
            if (!simpleContactRepository.existsById(contactId)) {
                throw new OurException("Contact not found", 404);
            }

            // Delete contact
            int deleted = contactCommandRepository.deleteContactById(contactId);
            
            if (deleted == 0) {
                throw new OurException("Failed to delete contact", 500);
            }

            logger.info("Contact {} deleted successfully", contactId);

            response.setStatusCode(200);
            response.setMessage("Contact deleted successfully");

            return response;
        } catch (OurException e) {
            logger.error("Error in deleteContact: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            logger.error("Unexpected error in deleteContact: {}", e.getMessage(), e);
            throw new OurException("Failed to delete contact", 500);
        }
    }
}