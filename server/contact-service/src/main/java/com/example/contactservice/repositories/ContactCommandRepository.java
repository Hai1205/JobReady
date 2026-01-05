package com.example.contactservice.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.example.contactservice.entities.Contact;
import com.example.contactservice.entities.Contact.ContactStatus;

import java.time.LocalDateTime;
import java.util.UUID;

@Repository
public interface ContactCommandRepository extends JpaRepository<Contact, UUID> {

        @Modifying
        @Transactional
        @Query("UPDATE Contact u SET u.status = :status WHERE u.id = :contactId")
        int updateContactStatus(@Param("contactId") UUID contactId,
                        @Param("status") ContactStatus status);

        @Modifying
        @Transactional
        @Query("UPDATE Contact c SET c.status = :status, c.resolvedBy = :resolvedBy, c.resolvedAt = :resolvedAt WHERE c.id = :contactId")
        int updateResolvedFields(@Param("contactId") UUID contactId,
                        @Param("status") ContactStatus status,
                        @Param("resolvedBy") UUID resolvedBy,
                        @Param("resolvedAt") LocalDateTime resolvedAt);

        @Modifying
        @Transactional
        @Query(value = "INSERT INTO contacts (id, name, email, plan, phone, message, status, created_at, updated_at) " +
                        "VALUES (:id, :name, :email, :plan, :phone, :message, :status, :createdAt, :updatedAt)", nativeQuery = true)
        int insertContact(@Param("id") UUID id,
                        @Param("name") String name,
                        @Param("email") String email,
                        @Param("plan") String plan,
                        @Param("phone") String phone,
                        @Param("message") String message,
                        @Param("status") String status,
                        @Param("createdAt") LocalDateTime createdAt,
                        @Param("updatedAt") LocalDateTime updatedAt);

        @Modifying
        @Transactional
        @Query("DELETE FROM Contact c WHERE c.id = :contactId")
        int deleteContactById(@Param("contactId") UUID contactId);
}
