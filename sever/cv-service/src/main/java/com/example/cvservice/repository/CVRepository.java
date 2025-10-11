package com.example.cvservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.cvservice.entity.CV;

import java.util.Optional;

@Repository
public interface CVRepository extends JpaRepository<CV, Long> {
    // Query by embedded personalInfo.email
    Optional<CV> findByPersonalInfoEmail(String email);

    boolean existsByPersonalInfoEmail(String email);

    Optional<CV> findByTitle(String title);
}