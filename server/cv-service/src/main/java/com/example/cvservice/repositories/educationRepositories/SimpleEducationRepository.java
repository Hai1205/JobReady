package com.example.cvservice.repositories.educationRepositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.cvservice.entities.Education;

import java.util.UUID;


@Repository
public interface SimpleEducationRepository extends JpaRepository<Education, UUID> {}
