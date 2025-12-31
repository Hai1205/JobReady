package com.example.paymentservice.controllers;

import com.example.paymentservice.dtos.response.Response;
import com.example.paymentservice.services.apis.PaymentApi;

import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.view.RedirectView;

@RestController
@RequestMapping("/api/v1/payments")
public class PaymentController {

    @Autowired
    private PaymentApi paymentApi;

    @Value("${CLIENT_URL}")
    private String frontendUrl;

    @GetMapping("/invoices")
    public ResponseEntity<Response> getAllInvoices() {
        Response response = paymentApi.getAllInvoices();

        return ResponseEntity.status(response.getStatusCode()).body(response);
    }

    @GetMapping("/invoices/{invoiceId}")
    public ResponseEntity<Response> getInvoice(@PathVariable UUID invoiceId) {
        Response response = paymentApi.getInvoiceById(invoiceId);

        return ResponseEntity.status(response.getStatusCode()).body(response);
    }

    @GetMapping("/users/{userId}/invoices")
    public ResponseEntity<Response> getUserInvoices(@PathVariable UUID userId) {
        Response response = paymentApi.getUserInvoices(userId);

        return ResponseEntity.status(response.getStatusCode()).body(response);
    }

    @PostMapping("/momo/{userId}/{planId}")
    public ResponseEntity<Response> createMomoPayment(@PathVariable UUID userId, @PathVariable UUID planId) {
        Response response = paymentApi.createMoMoPayment(userId, planId);

        return ResponseEntity.status(response.getStatusCode()).body(response);
    }

    @PostMapping("/momo/ipn")
    public ResponseEntity<Response> handleMoMoIPN(@RequestPart("data") String dataJson) {
        Response response = paymentApi.createMoMoIPN(dataJson);

        return ResponseEntity.status(response.getStatusCode()).body(response);
    }

    @GetMapping("/momo/callback")
    public RedirectView handleMoMoCallback(
            @RequestParam(required = false) String orderId,
            @RequestParam(required = false) Integer resultCode,
            @RequestParam(required = false) String message) {

        if (resultCode != null && resultCode == 0) {
            return new RedirectView(frontendUrl + "/payment/success?orderId=" + orderId + "&paymentMethod=momo");
        } else {
            return new RedirectView(frontendUrl + "/payment/failed?orderId=" + orderId + "&error="
                    + (message != null ? message : "payment_failed") + "&paymentMethod=momo");
        }
    }

    @GetMapping("/momo/query/{orderId}")
    public ResponseEntity<Response> queryMoMoTransaction(@PathVariable String orderId) {
        Response response = paymentApi.queryMoMoTransaction(orderId);

        return ResponseEntity.status(response.getStatusCode()).body(response);
    }

    @PostMapping("/paypal/{userId}/{planId}")
    public ResponseEntity<Response> createPayPalPayment(@PathVariable UUID userId, @PathVariable UUID planId) {
        Response response = paymentApi.createPayPalPayment(userId, planId);

        return ResponseEntity.status(response.getStatusCode()).body(response);
    }

    @GetMapping("/paypal/success")
    public RedirectView handlePayPalSuccess(
            @RequestParam("paymentId") String paymentId,
            @RequestParam("PayerID") String payerId) {

        Response response = paymentApi.executePayPalPayment(paymentId, payerId);

        if (response.isSuccess()) {
            return new RedirectView(
                    frontendUrl + "/payment/success?paymentId=" + paymentId + "&paymentMethod=paypal");
        } else {
            return new RedirectView(frontendUrl + "/payment/failed?paymentId=" + paymentId
                    + "&error=execution_failed&paymentMethod=paypal");
        }
    }

    @GetMapping("/paypal/cancel")
    public RedirectView handlePayPalCancel(
            @RequestParam(required = false, value = "token") String paymentId) {
        return new RedirectView(frontendUrl + "/payment/failed?paymentId="
                + (paymentId != null ? paymentId : "unknown") + "&error=cancelled&paymentMethod=paypal");
    }

    @GetMapping("/paypal/{paymentId}")
    public ResponseEntity<Response> getPayPalPaymentDetails(@PathVariable String paymentId) {
        Response response = paymentApi.getPayPalPaymentDetails(paymentId);

        return ResponseEntity.status(response.getStatusCode()).body(response);
    }

    @PostMapping("/vnpay/{userId}/{planId}")
    public ResponseEntity<Response> createVnPayPayment(@PathVariable UUID userId, @PathVariable UUID planId) {
        Response response = paymentApi.createVnPayPayment(userId, planId);

        return ResponseEntity.status(response.getStatusCode()).body(response);
    }

    @GetMapping("/vnpay/callback")
    public RedirectView handleVnPayCallback(@RequestParam Map<String, String> params) {
        String responseCode = params.get("vnp_ResponseCode");
        String txnRef = params.get("vnp_TxnRef");
        String amount = params.get("vnp_Amount");

        Response verifyResponse = paymentApi.verifyVnPayCallback(params);

        if (!verifyResponse.isSuccess()) {
            return new RedirectView(frontendUrl + "/payment/failed?txnRef=" + txnRef
                    + "&error=invalid_signature&paymentMethod=vnpay");
        }

        if ("00".equals(responseCode)) {
            return new RedirectView(frontendUrl + "/payment/success?txnRef=" + txnRef + "&paymentMethod=vnpay");
        } else {
            return new RedirectView(frontendUrl + "/payment/failed?txnRef=" + txnRef + "&error=code_"
                    + responseCode + "&paymentMethod=vnpay");
        }
    }

    @GetMapping("/health")
    public ResponseEntity<Response> health() {
        Response response = new Response();
        response.setStatusCode(200);
        response.setMessage("Payment Service is running");

        return ResponseEntity.status(response.getStatusCode()).body(response);
    }

    @GetMapping("/stats/revenue")
    public ResponseEntity<Response> getRevenueStats() {
        Response response = paymentApi.getRevenueStats();
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }
}