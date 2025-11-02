package com.example.cvservice.repositoryies;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.cvservice.entities.Experience;

import java.util.UUID;

@Repository
public interface ExperienceRepository extends JpaRepository<Experience, UUID> {
}