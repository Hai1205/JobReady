package com.example.userservice.mappers;

import com.example.userservice.dtos.PlanDto;
import com.example.userservice.entities.Plan;

import org.springframework.stereotype.Component;

@Component
public class PlanMapper {

    /**
     * Maps Plan entity to PlanDto
     */
    public PlanDto toDto(Plan plan) {
        if (plan == null) {
            return null;
        }

        PlanDto dto = new PlanDto();
        dto.setId(plan.getId());
        dto.setType(plan.getType());
        dto.setTitle(plan.getTitle());
        dto.setPrice(plan.getPrice());
        dto.setCurrency(plan.getCurrency());
        dto.setPeriod(plan.getPeriod());
        dto.setDescription(plan.getDescription());
        dto.setFeatures(plan.getFeatures());
        dto.setRecommended(plan.isRecommended());
        dto.setPopular(plan.isPopular());

        return dto;
    }

    /**
     * Maps PlanDto to Plan entity
     */
    public Plan toEntity(PlanDto dto) {
        if (dto == null) {
            return null;
        }

        Plan plan = new Plan();
        plan.setId(dto.getId()); // Set ID for updates
        plan.setType(dto.getType());
        plan.setTitle(dto.getTitle());
        plan.setPrice(dto.getPrice());
        plan.setCurrency(dto.getCurrency());
        plan.setPeriod(dto.getPeriod());
        plan.setDescription(dto.getDescription());
        plan.setFeatures(dto.getFeatures());
        plan.setRecommended(dto.isRecommended());
        plan.setPopular(dto.isPopular());

        return plan;
    }
}
