package com.example.paymentservice.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.example.paymentservice.entities.Invoice;
import com.example.paymentservice.entities.Invoice.InvoiceStatus;

import java.util.UUID;

@Repository
public interface InvoiceCommandRepository extends JpaRepository<Invoice, UUID> {

    /**
     * Insert invoice mới vào database
     */
    @Modifying
    @Transactional
    @Query(value = "INSERT INTO invoices (user_id, plan_name, price, currency, status, payment_method, transaction_id, billing_date, description) "
            +
            "VALUES (:userId, :planTitle, :price, :currency, :status, :paymentMethod, :transactionId, :billingDate, :description)", nativeQuery = true)
    Invoice insertInvoice(
            @Param("userId") UUID userId,
            @Param("planTitle") String planTitle,
            @Param("price") Long price,
            @Param("currency") String currency,
            @Param("status") InvoiceStatus status,
            @Param("paymentMethod") String paymentMethod,
            @Param("transactionId") String transactionId,
            @Param("billingDate") String billingDate,
            @Param("description") String description);

    @Modifying
    @Transactional
    @Query("UPDATE Invoice i SET i.status = :status WHERE i.transactionId = :transactionId")
    int updateStatusByTransactionId(
            @Param("transactionId") String transactionId,
            @Param("status") InvoiceStatus status);
}
