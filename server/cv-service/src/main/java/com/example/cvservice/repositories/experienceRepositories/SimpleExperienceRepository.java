package com.example.cvservice.repositories.experienceRepositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.cvservice.entities.Experience;

import java.util.UUID;

@Repository
public interface SimpleExperienceRepository extends JpaRepository<Experience, UUID> {}