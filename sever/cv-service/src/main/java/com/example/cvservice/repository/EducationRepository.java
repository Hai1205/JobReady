package com.example.cvservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.cvservice.entity.Education;

import java.util.UUID;

@Repository
public interface EducationRepository extends JpaRepository<Education, UUID> {
}