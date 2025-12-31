package com.example.paymentservice.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.paymentservice.entities.Invoice;

import java.util.UUID;

@Repository
public interface SimpleInvoiceRepository extends JpaRepository<Invoice, UUID> {
    // - Optional<Invoice> findById(UUID id)
    // - boolean existsById(UUID id)
    // - Invoice save(Invoice entity)
    // - void deleteById(UUID id)
    // - List<Invoice> findAll()
}
