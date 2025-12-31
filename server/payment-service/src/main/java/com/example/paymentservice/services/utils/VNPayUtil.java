package com.example.paymentservice.services.utils;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class VNPayUtil {

    public static String buildHashData(Map<String, String> params) {
        // Sắp xếp theo alphabet
        List<String> fieldNames = new ArrayList<>(params.keySet());
        Collections.sort(fieldNames);
        
        StringBuilder hashData = new StringBuilder();
        Iterator<String> itr = fieldNames.iterator();
        
        while (itr.hasNext()) {
            String fieldName = itr.next();
            String fieldValue = params.get(fieldName);
            
            if (fieldValue != null && !fieldValue.isEmpty()) {
                hashData.append(fieldName);
                hashData.append('=');
                hashData.append(fieldValue); // KHÔNG encode ở đây!
                
                if (itr.hasNext()) {
                    hashData.append('&');
                }
            }
        }
        
        return hashData.toString();
    }

    /**
     * Build query string với URL encoding
     * Format: key1=value1&key2=value2&key3=value3 (các value được encode)
     */
    public static String buildQueryString(Map<String, String> params) {
        // Sắp xếp theo alphabet
        List<String> fieldNames = new ArrayList<>(params.keySet());
        Collections.sort(fieldNames);
        
        StringBuilder query = new StringBuilder();
        Iterator<String> itr = fieldNames.iterator();
        
        while (itr.hasNext()) {
            String fieldName = itr.next();
            String fieldValue = params.get(fieldName);
            
            if (fieldValue != null && !fieldValue.isEmpty()) {
                try {
                    query.append(URLEncoder.encode(fieldName, StandardCharsets.UTF_8.toString()));
                    query.append('=');
                    query.append(URLEncoder.encode(fieldValue, StandardCharsets.UTF_8.toString()));
                    
                    if (itr.hasNext()) {
                        query.append('&');
                    }
                } catch (Exception e) {
                    throw new RuntimeException("Error encoding query string", e);
                }
            }
        }
        
        return query.toString();
    }

    /**
     * Tính HMAC SHA512
     */
    public static String hmacSHA512(String key, String data) {
        try {
            Mac hmac512 = Mac.getInstance("HmacSHA512");
            SecretKeySpec secretKey = new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), "HmacSHA512");
            hmac512.init(secretKey);
            byte[] result = hmac512.doFinal(data.getBytes(StandardCharsets.UTF_8));
            
            StringBuilder sb = new StringBuilder(2 * result.length);
            for (byte b : result) {
                sb.append(String.format("%02x", b & 0xff));
            }
            return sb.toString();
        } catch (Exception e) {
            throw new RuntimeException("Error generating HMAC SHA512", e);
        }
    }

    /**
     * Verify signature từ VNPay callback
     */
    public static boolean verifySignature(Map<String, String> params, String secureHash, String secretKey) {
        String hashData = buildHashData(params);
        String calculatedHash = hmacSHA512(secretKey, hashData);
        return calculatedHash.equalsIgnoreCase(secureHash);
    }
}