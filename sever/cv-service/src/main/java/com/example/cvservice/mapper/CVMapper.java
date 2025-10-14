package com.example.cvservice.mapper;

import com.example.cvservice.dto.CVDto;
import com.example.cvservice.entity.CV;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface CVMapper {
    CVDto toDto(CV cv);

    CV toEntity(CVDto cvDto);
}