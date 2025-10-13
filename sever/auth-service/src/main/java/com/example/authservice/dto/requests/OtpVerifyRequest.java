package com.example.authservice.dto.requests;

public class OtpVerifyRequest {
    private String otp;

    public OtpVerifyRequest() {
    }

    public OtpVerifyRequest(String otp) {
        this.otp = otp;
    }

    public String getOtp() {
        return otp;
    }

    public void setOtp(String otp) {
        this.otp = otp;
    }
}
