package com.example.authservice.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import jakarta.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Component
public class JwtUtil {

    @Value("${JWT_PRIVATE_KEY}")
    private String privateKeyStr;
    private PrivateKey privateKey;

    @Value("${JWT_PUBLIC_KEY}")
    private String publicKeyStr;
    private PublicKey publicKey;

    private static final long EXPIRATION_TIME = 1000 * 60 * 60 * 24 * 7; // 7 days

    @PostConstruct
    public void init() {
        this.privateKey = (PrivateKey) getKey(privateKeyStr, true);
        this.publicKey = (PublicKey) getKey(publicKeyStr, false);
    }

    private Key getKey(String key, boolean isPrivate) {
        try {
            // Loại bỏ tất cả các khoảng trắng và xuống dòng từ khóa
            String cleanedKey = key.replaceAll("\\s", "");

            // Giải mã Base64
            byte[] decoded;
            try {
                decoded = Base64.getDecoder().decode(cleanedKey);
            } catch (IllegalArgumentException e) {
                throw new RuntimeException("Không thể giải mã Base64 cho " +
                        (isPrivate ? "khóa riêng tư" : "khóa công khai") +
                        ". Đảm bảo rằng khóa ở định dạng Base64 hợp lệ.", e);
            }

            KeyFactory keyFactory = KeyFactory.getInstance("RSA");

            try {
                if (isPrivate) {
                    PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(decoded);
                    return keyFactory.generatePrivate(keySpec);
                } else {
                    X509EncodedKeySpec keySpec = new X509EncodedKeySpec(decoded);
                    return keyFactory.generatePublic(keySpec);
                }
            } catch (Exception e) {
                throw new RuntimeException("Không thể tạo " +
                        (isPrivate ? "khóa riêng tư" : "khóa công khai") +
                        " từ dữ liệu được cung cấp. Đảm bảo khóa có định dạng chính xác " +
                        (isPrivate ? "PKCS8" : "X509") + ".", e);
            }
        } catch (Exception e) {
            throw new RuntimeException("Không thể tải " +
                    (isPrivate ? "khóa riêng tư" : "khóa công khai") +
                    ". Chi tiết: " + e.getMessage(), e);
        }
    }

    public String generateToken(String userId, String username, String role) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", userId);
        claims.put("username", username);
        claims.put("role", role);

        return Jwts.builder()
                .claims(claims)
                .subject(username)
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .signWith(privateKey)
                .compact();
    }

    public String extractUsername(String token) {
        return extractClaims(token, Claims::getSubject);
    }

    public Date extractExpiration(String token) {
        return extractClaims(token, Claims::getExpiration);
    }

    private <T> T extractClaims(String token, Function<Claims, T> claimsResolver) {
        return claimsResolver.apply(Jwts.parser().verifyWith(publicKey).build().parseSignedClaims(token).getPayload());
    }

    private Boolean isTokenExpired(String token) {
        return extractClaims(token, Claims::getExpiration).before(new Date());
    }

    public Boolean validateToken(String token, String username) {
        final String extractedUsername = extractUsername(token);
        return (extractedUsername.equals(username) && !isTokenExpired(token));
    }
}