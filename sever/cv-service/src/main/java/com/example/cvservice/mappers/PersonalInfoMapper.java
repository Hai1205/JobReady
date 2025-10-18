package com.example.cvservice.mappers;

import com.example.cvservice.dtos.PersonalInfoDto;
import com.example.cvservice.entities.PersonalInfo;

import org.springframework.stereotype.Component;

@Component
public class PersonalInfoMapper {

    public PersonalInfoDto toDto(PersonalInfo personalInfo) {
        if (personalInfo == null)
            return null;
        PersonalInfoDto dto = new PersonalInfoDto();
        dto.setId(personalInfo.getId());
        dto.setFullname(personalInfo.getFullname());
        dto.setEmail(personalInfo.getEmail());
        dto.setPhone(personalInfo.getPhone());
        dto.setLocation(personalInfo.getLocation());
        dto.setSummary(personalInfo.getSummary());
        dto.setAvatarUrl(personalInfo.getAvatarUrl());
        dto.setAvatarPublicId(personalInfo.getAvatarPublicId());
        return dto;
    }

    public PersonalInfo toEntity(PersonalInfoDto personalInfoDto) {
        if (personalInfoDto == null)
            return null;
        PersonalInfo entity = new PersonalInfo();
        entity.setId(personalInfoDto.getId());
        entity.setFullname(personalInfoDto.getFullname());
        entity.setEmail(personalInfoDto.getEmail());
        entity.setPhone(personalInfoDto.getPhone());
        entity.setLocation(personalInfoDto.getLocation());
        entity.setSummary(personalInfoDto.getSummary());
        entity.setAvatarUrl(personalInfoDto.getAvatarUrl());
        entity.setAvatarPublicId(personalInfoDto.getAvatarPublicId());
        return entity;
    }
}