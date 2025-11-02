package com.example.cvservice.repositoryies;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.cvservice.entities.PersonalInfo;

import java.util.UUID;

@Repository
public interface PersonalInfoRepository extends JpaRepository<PersonalInfo, UUID> {
}