package com.example.authservice.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Random;
import java.util.concurrent.TimeUnit;

@Service
public class OtpService {

    private static final Logger logger = LoggerFactory.getLogger(OtpService.class);

    private static final String OTP_PREFIX = "OTP_";
    private static final int OTP_EXPIRATION = 3; // 3 minutes
    private static final int OTP_LENGTH = 6;

    private final RedisTemplate<String, Object> redisTemplate;

    public OtpService(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    /**
     * Tạo OTP mới cho email cụ thể
     * 
     * @param email Email cần tạo OTP
     * @return OTP được tạo
     */
    public String generateOtp(String email) {
        try {
            String otp = generateRandomOtp(OTP_LENGTH);
            saveOtp(email, otp);
            return otp;
        } catch (Exception e) {
            logger.error("Error generating OTP for email: {}", email, e);
            throw new RuntimeException("Failed to generate OTP: " + e.getMessage());
        }
    }

    /**
     * Tạo OTP ngẫu nhiên
     * 
     * @param length Độ dài của OTP
     * @return Chuỗi OTP
     */
    private String generateRandomOtp(int length) {
        StringBuilder otp = new StringBuilder();
        Random random = new Random();

        for (int i = 0; i < length; i++) {
            otp.append(random.nextInt(10));
        }

        return otp.toString();
    }

    /**
     * Lưu OTP cho email cụ thể vào Redis với TTL
     */
    public void saveOtp(String email, String otp) {
        try {
            String key = OTP_PREFIX + email;
            redisTemplate.opsForValue().set(key, otp);
            redisTemplate.expire(key, OTP_EXPIRATION, TimeUnit.MINUTES);
            logger.info("OTP saved for email: {}, expires in {} minutes", email, OTP_EXPIRATION);
        } catch (Exception e) {
            logger.error("Error saving OTP for email: {}", email, e);
            throw new RuntimeException("Failed to save OTP: " + e.getMessage());
        }
    }

    /**
     * Xác thực OTP cho email cụ thể
     */
    public boolean validateOtp(String email, String otp) {
        try {
            String key = OTP_PREFIX + email;
            Object savedOtp = redisTemplate.opsForValue().get(key);

            if (savedOtp != null && savedOtp.toString().equals(otp)) {
                // Xóa OTP sau khi xác thực thành công
                redisTemplate.delete(key);
                logger.info("OTP verified successfully for email: {}", email);
                return true;
            }

            logger.warn("Invalid OTP for email: {}", email);
            return false;
        } catch (Exception e) {
            logger.error("Error verifying OTP for email: {}", email, e);
            throw new RuntimeException("Failed to verify OTP: " + e.getMessage());
        }
    }

    /**
     * Kiểm tra xem OTP có tồn tại không
     */
    public boolean otpExists(String email) {
        try {
            String key = OTP_PREFIX + email;
            return Boolean.TRUE.equals(redisTemplate.hasKey(key));
        } catch (Exception e) {
            logger.error("Error checking OTP existence for email: {}", email, e);
            return false;
        }
    }

    /**
     * Xóa OTP cho email cụ thể
     */
    public void deleteOtp(String email) {
        try {
            String key = OTP_PREFIX + email;
            redisTemplate.delete(key);
            logger.info("OTP deleted for email: {}", email);
        } catch (Exception e) {
            logger.error("Error deleting OTP for email: {}", email, e);
        }
    }
}