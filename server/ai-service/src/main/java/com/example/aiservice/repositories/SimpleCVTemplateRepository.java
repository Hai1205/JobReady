package com.example.aiservice.repositories;

import com.example.aiservice.entities.CVTemplateEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SimpleCVTemplateRepository extends JpaRepository<CVTemplateEntity, String> {
    boolean existsById(String id);
}
