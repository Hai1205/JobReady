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

    // Access Token: 15 phút (ngắn hạn)
    private static final long ACCESS_TOKEN_EXPIRATION = 1000 * 60 * 15; // 15 minutes

    // Refresh Token: 7 ngày (dài hạn)
    private static final long REFRESH_TOKEN_EXPIRATION = 1000 * 60 * 60 * 24 * 7; // 7 days

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

    /**
     * Tạo Access Token (JWT ngắn hạn - 15 phút)
     * 
     * @param userId   ID của user
     * @param username Username của user
     * @param role     Role của user (ADMIN, USER, etc.)
     * @return Access Token string
     */
    public String generateAccessToken(String userId, String email, String role, String username) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", userId);
        claims.put("email", email);
        if (username != null) {
            claims.put("username", username);
        }
        claims.put("role", role);
        claims.put("tokenType", "ACCESS"); // Đánh dấu đây là access token

        String subject = email != null ? email : username;

        return Jwts.builder()
                .claims(claims)
                .subject(subject)
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + ACCESS_TOKEN_EXPIRATION))
                .signWith(privateKey)
                .compact();
    }

    public String generateAccessToken(String userId, String username, String role) {
        return generateAccessToken(userId, username, role, username);
    }

    /**
     * Tạo Refresh Token (JWT dài hạn - 7 ngày)
     * 
     * @param userId   ID của user
     * @param username Username của user
     * @return Refresh Token string
     */
    public String generateRefreshToken(String userId, String email, String username) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", userId);
        claims.put("email", email);
        if (username != null) {
            claims.put("username", username);
        }
        claims.put("tokenType", "REFRESH"); // Đánh dấu đây là refresh token

        String subject = email != null ? email : username;

        return Jwts.builder()
                .claims(claims)
                .subject(subject)
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + REFRESH_TOKEN_EXPIRATION))
                .signWith(privateKey)
                .compact();
    }

    public String generateRefreshToken(String userId, String username) {
        return generateRefreshToken(userId, username, username);
    }

    /**
     * Tạo Token (deprecated - sử dụng generateAccessToken thay thế)
     * Giữ lại để backward compatibility
     */
    @Deprecated
    public String generateToken(String userId, String username, String role) {
        return generateAccessToken(userId, username, role, username);
    }

    /**
     * Extract username từ token
     */
    public String extractUsername(String token) {
        return extractClaims(token, claims -> {
            String usernameClaim = claims.get("username", String.class);
            if (usernameClaim != null) {
                return usernameClaim;
            }
            return claims.getSubject();
        });
    }

    /**
     * Extract userId từ token
     */
    public String extractUserId(String token) {
        return extractClaims(token, claims -> claims.get("userId", String.class));
    }

    /**
     * Extract role từ token (chỉ có trong access token)
     */
    public String extractRole(String token) {
        return extractClaims(token, claims -> claims.get("role", String.class));
    }

    /**
     * Extract email từ token
     */
    public String extractEmail(String token) {
        return extractClaims(token, claims -> {
            String emailClaim = claims.get("email", String.class);
            if (emailClaim != null) {
                return emailClaim;
            }
            return claims.getSubject();
        });
    }

    /**
     * Extract token type (ACCESS hoặc REFRESH)
     */
    public String extractTokenType(String token) {
        return extractClaims(token, claims -> claims.get("tokenType", String.class));
    }

    /**
     * Extract expiration date từ token
     */
    public Date extractExpiration(String token) {
        return extractClaims(token, Claims::getExpiration);
    }

    /**
     * Extract claims từ token
     */
    private <T> T extractClaims(String token, Function<Claims, T> claimsResolver) {
        return claimsResolver.apply(Jwts.parser().verifyWith(publicKey).build().parseSignedClaims(token).getPayload());
    }

    /**
     * Kiểm tra token đã hết hạn chưa
     */
    private Boolean isTokenExpired(String token) {
        return extractClaims(token, Claims::getExpiration).before(new Date());
    }

    /**
     * Validate token (kiểm tra username và expiration)
     * 
     * @param token    JWT token cần validate
     * @param username Username để so sánh
     * @return true nếu token hợp lệ
     */
    public Boolean validateToken(String token, String username) {
        final String extractedUsername = extractUsername(token);
        return (extractedUsername.equals(username) && !isTokenExpired(token));
    }

    /**
     * Validate Refresh Token
     * Kiểm tra token type phải là REFRESH và chưa hết hạn
     * 
     * @param refreshToken Refresh token cần validate
     * @return true nếu refresh token hợp lệ
     */
    public Boolean validateRefreshToken(String refreshToken) {
        try {
            String tokenType = extractTokenType(refreshToken);
            // Kiểm tra token type phải là REFRESH
            if (!"REFRESH".equals(tokenType)) {
                return false;
            }
            // Kiểm tra token chưa hết hạn
            return !isTokenExpired(refreshToken);
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Validate Access Token
     * Kiểm tra token type phải là ACCESS và chưa hết hạn
     * 
     * @param accessToken Access token cần validate
     * @param username    Username để so sánh
     * @return true nếu access token hợp lệ
     */
    public Boolean validateAccessToken(String accessToken, String username) {
        try {
            String tokenType = extractTokenType(accessToken);
            // Kiểm tra token type phải là ACCESS
            if (!"ACCESS".equals(tokenType)) {
                return false;
            }
            // Kiểm tra username và expiration
            return validateToken(accessToken, username);
        } catch (Exception e) {
            return false;
        }
    }
}