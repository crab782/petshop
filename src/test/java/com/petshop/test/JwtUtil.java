package com.petshop.test;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

public class JwtUtil {

    private static final String JWT_SECRET = "petshop-default-secret-key-for-jwt-token-generation-and-validation-must-be-at-least-256-bits";
    private static final int JWT_EXPIRATION_MS = 86400000; // 24 hours

    private static SecretKey getSigningKey() {
        byte[] keyBytes = JWT_SECRET.getBytes(StandardCharsets.UTF_8);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    public static String generateToken(String username) {
        return Jwts.builder()
                .subject(username)
                .issuedAt(new Date())
                .expiration(new Date((new Date()).getTime() + JWT_EXPIRATION_MS))
                .signWith(getSigningKey())
                .compact();
    }

    public static String getUsernameFromToken(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .getSubject();
    }

    public static boolean validateToken(String token) {
        try {
            Jwts.parser().verifyWith(getSigningKey()).build().parseSignedClaims(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    // 生成不同角色的测试token
    public static String generateUserToken() {
        return generateToken("test_user@example.com");
    }

    public static String generateMerchantToken() {
        return generateToken("test_merchant@example.com");
    }

    public static String generateAdminToken() {
        return generateToken("admin@example.com");
    }
}
