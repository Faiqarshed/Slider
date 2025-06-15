package com.example.Sliderbackend.Message;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

import java.nio.charset.StandardCharsets;
import java.util.Map;

import javax.crypto.SecretKey;

@Component
public class JwtHandshakeInterceptor implements HandshakeInterceptor {

    private final String SECRET_KEY = "mysecretkeyisalmostexactly32characterslong";
    private final long EXPIRATION_TIME = 86400000; // 24 hours in milliseconds
    private final long CLOCK_SKEW_SECONDS = 300; // 5 minutes in seconds

    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(SECRET_KEY.getBytes());
    }


    @Override
    public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response,
                                   WebSocketHandler wsHandler, Map<String, Object> attributes) {

        String authHeader = request.getHeaders().getFirst("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            try {
                Claims claims = Jwts.parser()
                        .verifyWith(getSigningKey())
                        .setAllowedClockSkewSeconds(CLOCK_SKEW_SECONDS)
                        .build()
                        .parseSignedClaims(token)
                        .getPayload();

                String username = claims.getSubject(); // ✅ This is the subject inside the claims
                attributes.put("username", username); // 👈 Pass to WebSocket session
            } catch (Exception e) {
                System.out.println("Invalid JWT Token");
                return false;
            }
        }

        return true;
    }

    @Override
    public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response,
                               WebSocketHandler wsHandler, Exception exception) {
    }
}
