package com.example.contactservice.repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.contactservice.entities.Contact;
import com.example.contactservice.entities.Contact.ContactStatus;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ContactQueryRepository extends JpaRepository<Contact, UUID> {

        @Query("SELECT c FROM Contact c WHERE c.id = :contactId")
        Optional<Contact> findContactById(@Param("contactId") UUID contactId);

        @Query("SELECT c FROM Contact c")
        Page<Contact> findAllContacts(Pageable pageable);
}
