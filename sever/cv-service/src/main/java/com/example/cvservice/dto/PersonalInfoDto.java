package com.example.cvservice.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PersonalInfoDto {
    private String fullname;
    private String email;
    private String phone;
    private String location;
    private String summary;
}
