package com.example.sim_registration.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;


import javax.crypto.SecretKey;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.UUID;

@Slf4j
@Service
public class JwtService {

    private final SecretKey signingKey;
    private final long expirationMinutes;

    public JwtService(
            @Value("${app.jwt.secret}") String secret,
            @Value("${app.jwt.expiration-minutes}") long expirationMinutes) {

        this.signingKey = Keys.hmacShaKeyFor(secret.getBytes());
        this.expirationMinutes = expirationMinutes;
    }

    public String generateToken(UUID agentId, String username){

        Instant now = Instant.now();
        Instant expiry = now.plus(expirationMinutes, ChronoUnit.MINUTES);

        return Jwts.builder()
                .subject(agentId.toString())
                .claim("username", username)
                .issuedAt(Date.from(now))
                .expiration(Date.from(expiry))
                .signWith(signingKey)
                .compact();
    }

    public UUID extractAgentId(String token){
        Claims claims = parseClaims(token);
        return UUID.fromString(claims.getSubject());
    } // given a token this method pulls the agents UUID back out,
    // The filter will call on every incoming request to find out who's making the call

    //parsing and verifying the token
    public  boolean isTokenValid(String token){
        try {
            parseClaims(token);
            return true;
        } catch (Exception e){
            // Only the failure type is logged (expired, bad signature, malformed), never the token itself
            log.debug("Token rejected: {}", e.getClass().getSimpleName());
            return false;
        }
    }

    //both public methods rly on this shared internal logic
    //verify signature against the key and read the Payload
    private Claims parseClaims(String token){
        return Jwts.parser()
                .verifyWith(signingKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();

    }
}
