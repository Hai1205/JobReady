package com.example.paymentservice.dtos.response;

import java.util.List;
import java.util.Map;

import com.example.paymentservice.dtos.*;
import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.*;

@Data
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Response {
    private int statusCode;
    private String message;

    private boolean success;
    private String paymentUrl;
    private String orderId;
    private String txnRef;
    private int resultCode;

    private UserDto user;
    private InvoiceDto invoice;
    private List<InvoiceDto> invoices;
    private RevenueStatsDto revenueStats;
    
    // Pagination and stats
    private Object pagination;
    private Map<String, Object> stats;

    // Generic data container for any other service-specific data
    private Map<String, Object> additionalData;

    public Response(int statusCode, String message) {
        this.statusCode = statusCode;
        this.message = message;
    }

    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }

    public Response() {
        this.statusCode = 200;
    }
}