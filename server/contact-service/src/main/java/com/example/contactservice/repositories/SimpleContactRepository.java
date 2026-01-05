package com.example.contactservice.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.contactservice.entities.Contact;

import java.util.UUID;

@Repository
public interface SimpleContactRepository extends JpaRepository<Contact, UUID> {
    // - Optional<SimpleContact> findById(UUID id)
    // - boolean existsById(UUID id)
    // - SimpleContact save(SimpleContact entity)
    // - void deleteById(UUID id)
    // - List<SimpleContact> findAll()
}
