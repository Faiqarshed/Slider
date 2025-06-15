package com.example.Sliderbackend;

import org.springframework.stereotype.Component;

import java.util.Date;

import javax.crypto.SecretKey;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

@Component
public class JwtUtil {
    private final String SECRET_KEY = "mysecretkeyisalmostexactly32characterslong";
    private final long EXPIRATION_TIME = 86400000; // 24 hours in milliseconds
    private final long CLOCK_SKEW_SECONDS = 300; // 5 minutes in seconds

    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(SECRET_KEY.getBytes());
    }

    public String generateToken(String email) {
        return Jwts.builder()
                .subject(email)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .signWith(getSigningKey())
                .compact();
    }

    public String extractEmail(String token) {
        try {
            return Jwts.parser()
                    .verifyWith(getSigningKey())
                    .setAllowedClockSkewSeconds(CLOCK_SKEW_SECONDS)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload()
                    .getSubject();
        } catch (ExpiredJwtException e) {
            // Log the error if needed
            // System.out.println("Token expired: " + e.getMessage());
            return null;
        } catch (Exception e) {
            // Log any other errors if needed
            // System.out.println("Error parsing token: " + e.getMessage());
            return null;
        }
    }

    private boolean isTokenExpired(String token) {
        try {
            Date expiration = Jwts.parser()
                    .verifyWith(getSigningKey())
                    .setAllowedClockSkewSeconds(CLOCK_SKEW_SECONDS)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload()
                    .getExpiration();

            return expiration.before(new Date());
        } catch (ExpiredJwtException e) {
            return true;
        } catch (Exception e) {
            return true;
        }
    }

    public boolean validateToken(String token, String email) {
        try {
            if (token == null || email == null) {
                return false;
            }

            String extractedEmail = extractEmail(token);
            if (extractedEmail == null) {
                return false;
            }

            return extractedEmail.equals(email) && !isTokenExpired(token);
        } catch (Exception e) {
            return false;
        }
    }
}