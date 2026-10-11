package com.example.demo.service;

import com.example.demo.model.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;

@Service
public class JwtService {

    // CHANGE THIS IN PRODUCTION! Use a strong, long secret (at least 64 characters)
    private static final String SECRET_KEY =
            "your-512-bit-secret-key-here-change-in-prod-123456789012345678901234567890123456789012345678901234567890";

    private final SecretKey key = Keys.hmacShaKeyFor(SECRET_KEY.getBytes());

    private static final long LOGIN_EXPIRATION_MS = 1000L * 60 * 60 * 24 * 7; // 7 days
    private static final long VERIFICATION_EXPIRATION_MS = 1000L * 60 * 15;   // 15 minutes

    /* ==================== MAIN LOGIN TOKENS ==================== */

    public String generateToken(User user) {
        return generateAccessToken(user);
    }

    public String generateAccessToken(User user) {
        Date now = new Date();
        Date expiry = new Date(now.getTime() + 24 * 60 * 60 * 1000); // 24 hours

        return Jwts.builder()
                .setSubject(user.getEmail())
                .claim("userId", user.getId())
                .claim("type", "access")
                .setIssuedAt(now)
                .setExpiration(expiry)
                .signWith(key, SignatureAlgorithm.HS512)
                .compact();
    }

    public String generateRefreshToken(User user) {
        Date now = new Date();
        Date expiry = new Date(now.getTime() + 7L * 24 * 60 * 60 * 1000); // 7 days

        return Jwts.builder()
                .setSubject(user.getEmail())
                .claim("userId", user.getId())
                .claim("type", "refresh")
                .setIssuedAt(now)
                .setExpiration(expiry)
                .signWith(key, SignatureAlgorithm.HS512)
                .compact();
    }

    /* ==================== TOKEN VALIDATION HELPERS ==================== */

    public boolean isAccessToken(String token) {
        return "access".equals(getClaimFromToken(token, "type", String.class));
    }

    public boolean isRefreshToken(String token) {
        return "refresh".equals(getClaimFromToken(token, "type", String.class));
    }

    public String getEmailFromToken(String token) {
        return getAllClaims(token).getSubject();
    }

    public String getTierFromToken(String token) {
        return getClaimFromToken(token, "tier", String.class);
    }

    public <T> T getClaimFromToken(String token, String claimName, Class<T> type) {
        try {
            return getAllClaims(token).get(claimName, type);
        } catch (Exception e) {
            return null;
        }
    }

    public boolean isTokenValid(String token, UserDetails userDetails) {
        try {
            String email = getEmailFromToken(token);
            return email.equals(userDetails.getUsername()) && !isTokenExpired(token);
        } catch (Exception e) {
            return false;
        }
    }

    private boolean isTokenExpired(String token) {
        return getAllClaims(token).getExpiration().before(new Date());
    }

    private Claims getAllClaims(String token) {
    return Jwts.parser()
        .verifyWith(key)
        .build()
        .parseSignedClaims(token)
        .getPayload();
}

    /* ==================== EMAIL VERIFICATION TOKEN ==================== */

    public String generateVerificationToken(User user) {
        Date now = new Date();
        Date expiry = new Date(now.getTime() + VERIFICATION_EXPIRATION_MS);

        return Jwts.builder()
                .setSubject(user.getEmail())
                .setIssuedAt(now)
                .setExpiration(expiry)
                .signWith(key, SignatureAlgorithm.HS512)
                .compact();
    }

    public String validateVerificationTokenAndGetEmail(String token) {
        try {
            return Jwts.parser()
                .verifyWith(key)   // key must be a SecretKey
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .getSubject();
        } catch (ExpiredJwtException e) {
            throw new RuntimeException("Verification token has expired");
        } catch (MalformedJwtException | UnsupportedJwtException | IllegalArgumentException e) {
            throw new RuntimeException("Invalid verification token");
        }
    }
}