package com.example.cvservice.repositories.cvRepositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.cvservice.entities.CV;

import java.util.UUID;

@Repository
public interface SimpleCVRepository extends JpaRepository<CV, UUID> {
    // - Optional<CV> findById(UUID id)
    // - boolean existsById(UUID id)
    // - CV save(CV entity)
    // - void deleteById(UUID id)
    // - List<CV> findAll()
}
