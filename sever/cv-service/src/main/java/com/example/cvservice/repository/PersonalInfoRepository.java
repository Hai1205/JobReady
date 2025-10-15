package com.example.cvservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.cvservice.entity.PersonalInfo;

import java.util.UUID;

@Repository
public interface PersonalInfoRepository extends JpaRepository<PersonalInfo, UUID> {
}