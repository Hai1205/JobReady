package com.example.paymentservice.dtos;

import java.util.List;
import java.util.UUID;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PlanDto {
    private UUID id;
    private String type;
    private String name;
    private Long price;
    private String currency;
    private String period;
    private String description;
    private List<String> features;
    private boolean isRecommended;
    private boolean isPopular;
}