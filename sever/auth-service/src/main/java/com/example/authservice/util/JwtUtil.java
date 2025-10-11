package com.example.authservice.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
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

@Component
public class JwtUtil {

    @Value("${JWT_PRIVATE_KEY}")
    private String privateKeyString;

    @Value("${JWT_PUBLIC_KEY}")
    private String publicKeyString;

    private PrivateKey privateKey;
    private PublicKey publicKey;
    private final long jwtExpiration = 86400000; // 24 hours

    private Key getKey(String keyPEM, String header, String footer, boolean isPrivate) {
        try {
            keyPEM = keyPEM
                    .replace(header, "")
                    .replace(footer, "")
                    .replaceAll("\\s", "");

            byte[] decoded = Base64.getDecoder().decode(keyPEM);
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");

            if (isPrivate) {
                PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(decoded);
                return keyFactory.generatePrivate(keySpec);
            } else {
                X509EncodedKeySpec keySpec = new X509EncodedKeySpec(decoded);
                return keyFactory.generatePublic(keySpec);
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to load " + (isPrivate ? "private" : "public") + " key", e);
        }
    }

    private PrivateKey getPrivateKey() {
        if (privateKey == null) {
            privateKey = (PrivateKey) getKey(privateKeyString,
                    "-----BEGIN PRIVATE KEY-----",
                    "-----END PRIVATE KEY-----",
                    true);
        }
        return privateKey;
    }

    private PublicKey getPublicKey() {
        if (publicKey == null) {
            publicKey = (PublicKey) getKey(publicKeyString,
                    "-----BEGIN PUBLIC KEY-----",
                    "-----END PUBLIC KEY-----",
                    false);
        }
        return publicKey;
    }

    public String generateToken(String username, String userId) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", userId);
        claims.put("username", username);
        return createToken(claims, username);
    }

    private String createToken(Map<String, Object> claims, String subject) {
        return Jwts.builder()
                .claims(claims)
                .subject(subject)
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + jwtExpiration))
                .signWith(getPrivateKey())
                .compact();
    }

    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    public <T> T extractClaim(String token, java.util.function.Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parser()
                .verifyWith(getPublicKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    private Boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    public Boolean validateToken(String token, String username) {
        final String extractedUsername = extractUsername(token);
        return (extractedUsername.equals(username) && !isTokenExpired(token));
    }
}