package com.example.cvservice.mapper;

import com.example.cvservice.dto.PersonalInfoDto;
import com.example.cvservice.entity.PersonalInfo;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface PersonalInfoMapper {
    PersonalInfoDto toDto(PersonalInfo personalInfo);

    PersonalInfo toEntity(PersonalInfoDto personalInfoDto);
}