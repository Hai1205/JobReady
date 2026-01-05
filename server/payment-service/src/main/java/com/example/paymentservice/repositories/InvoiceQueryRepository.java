package com.example.paymentservice.repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.paymentservice.entities.Invoice;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface InvoiceQueryRepository extends JpaRepository<Invoice, UUID> {

    @Query("SELECT i FROM Invoice i WHERE i.id = :invoiceId")
    Optional<Invoice> findInvoiceById(@Param("invoiceId") UUID invoiceId);

    @Query("SELECT i FROM Invoice i WHERE i.transactionId = :transactionId")
    Optional<Invoice> findByTransactionId(@Param("transactionId") String transactionId);

    @Query("SELECT i FROM Invoice i WHERE i.userId = :userId")
    List<Invoice> findByUserId(@Param("userId") UUID userId);

    @Query("SELECT i FROM Invoice i WHERE i.status = :status")
    List<Invoice> findByStatus(@Param("status") Invoice.InvoiceStatus status);

    @Query("SELECT i FROM Invoice i WHERE i.id = :invoiceId AND i.status = :status")
    List<Invoice> findByInvoiceIdAndStatus(@Param("invoiceId") UUID invoiceId,
            @Param("status") Invoice.InvoiceStatus status);

    @Query("SELECT COUNT(i) FROM Invoice i WHERE i.status = :status")
    long countByStatus(@Param("status") Invoice.InvoiceStatus status);

    @Query("SELECT i FROM Invoice i")
    Page<Invoice> findAllInvoices(Pageable pageable);
}
