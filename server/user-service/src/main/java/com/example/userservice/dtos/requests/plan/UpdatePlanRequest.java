package com.example.userservice.dtos.requests.plan;

import java.util.List;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdatePlanRequest {
    private String type;
    private String title;
    private Long price;
    private String currency;
    private String period;
    private String description;
    private List<String> features;
    private Boolean isRecommended;
    private Boolean isPopular;
}
