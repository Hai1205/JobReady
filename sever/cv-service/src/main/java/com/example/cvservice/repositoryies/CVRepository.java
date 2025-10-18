package com.example.cvservice.repositoryies;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.cvservice.entities.CV;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface CVRepository extends JpaRepository<CV, UUID> {
    Optional<CV> findByPersonalInfoEmail(String email);

    boolean existsByPersonalInfoEmail(String email);

    Optional<CV> findByTitle(String title);

    List<CV> findAllByUserId(UUID userId);
}