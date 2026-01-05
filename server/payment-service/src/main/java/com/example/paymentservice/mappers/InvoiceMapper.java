package com.example.paymentservice.mappers;

import com.example.paymentservice.dtos.InvoiceDto;
import com.example.paymentservice.entities.Invoice;
import com.example.paymentservice.entities.Invoice.InvoiceStatus;

import org.springframework.stereotype.Component;

@Component
public class InvoiceMapper {

    /**
     * Maps Invoice entity to InvoiceDto
     */
    public InvoiceDto toDto(Invoice invoice) {
        if (invoice == null) {
            return null;
        }

        InvoiceDto dto = new InvoiceDto();
        dto.setId(invoice.getId());
        dto.setUserId(invoice.getUserId());
        dto.setPlanTitle(invoice.getPlanTitle());
        dto.setAmount(invoice.getAmount());
        dto.setCurrency(invoice.getCurrency());
        dto.setStatus(invoice.getStatus() != null ? invoice.getStatus().name() : null);
        dto.setPaymentMethod(invoice.getPaymentMethod());
        dto.setTransactionId(invoice.getTransactionId());
        dto.setBillingDate(invoice.getBillingDate());
        dto.setPeriodStart(invoice.getPeriodStart());
        dto.setPeriodEnd(invoice.getPeriodEnd());
        dto.setDescription(invoice.getDescription());
        dto.setDownloadUrl(invoice.getDownloadUrl());

        return dto;
    }

    /**
     * Maps InvoiceDto to Invoice entity
     */
    public Invoice toEntity(InvoiceDto dto) {
        if (dto == null) {
            return null;
        }

        Invoice invoice = new Invoice();
        invoice.setId(dto.getId()); // Set ID for updates
        invoice.setUserId(dto.getUserId());
        invoice.setPlanTitle(dto.getPlanTitle());
        invoice.setAmount(dto.getAmount());
        invoice.setCurrency(dto.getCurrency());
        invoice.setStatus(dto.getStatus() != null ? InvoiceStatus.valueOf(dto.getStatus()) : null);
        invoice.setPaymentMethod(dto.getPaymentMethod());
        invoice.setTransactionId(dto.getTransactionId());
        invoice.setBillingDate(dto.getBillingDate());
        invoice.setPeriodStart(dto.getPeriodStart());
        invoice.setPeriodEnd(dto.getPeriodEnd());
        invoice.setDescription(dto.getDescription());
        invoice.setDownloadUrl(dto.getDownloadUrl());

        return invoice;
    }
}
