package com.example.cvservice.repositories.personalInfoRepositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.cvservice.entities.PersonalInfo;

import java.util.UUID;

@Repository
public interface SimplePersonalInfoRepository extends JpaRepository<PersonalInfo, UUID> { }