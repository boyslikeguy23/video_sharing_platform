package org.example.final_project.security;

import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

import java.security.SecureRandom;
import java.time.Duration;
import java.time.Instant;
import java.util.Base64;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class JwtTokenProviderTest {
    static String newSecret() {
        byte[] bytes = new byte[32];
        new SecureRandom().nextBytes(bytes);
        return Base64.getEncoder().encodeToString(bytes);
    }

    @Test
    void roundTripHasIdentityAndExpiry() {
        var provider = new JwtTokenProvider(newSecret(), Duration.ofHours(8));
        String token = provider.generateJwtToken(new UsernamePasswordAuthenticationToken("alice@example.test", null, List.of()));
        var claims = provider.parseToken(token);
        assertEquals("alice@example.test", claims.get("username"));
        assertEquals("video_sharing_platform", claims.getIssuer());
        assertTrue(claims.getExpiration().after(new Date()));
    }

    @Test
    void rotatedKeyRejectsPreviouslyIssuedTokens() {
        var oldProvider = new JwtTokenProvider(newSecret(), Duration.ofHours(8));
        String token = oldProvider.generateJwtToken(new UsernamePasswordAuthenticationToken("alice@example.test", null, List.of()));
        var newProvider = new JwtTokenProvider(newSecret(), Duration.ofHours(8));
        assertThrows(JwtException.class, () -> newProvider.parseToken(token));
    }

    @Test
    void rejectsExpiredWrongIssuerAndMissingIdentityTokens() {
        String secret = newSecret();
        var key = Keys.hmacShaKeyFor(Base64.getDecoder().decode(secret));
        var provider = new JwtTokenProvider(secret, Duration.ofHours(8));
        String expired = Jwts.builder().setIssuer("video_sharing_platform").claim("username", "alice@example.test")
                .setExpiration(Date.from(Instant.now().minusSeconds(60))).signWith(key).compact();
        String wrongIssuer = Jwts.builder().setIssuer("other-app").claim("username", "alice@example.test")
                .setExpiration(Date.from(Instant.now().plusSeconds(60))).signWith(key).compact();
        String missingIdentity = Jwts.builder().setIssuer("video_sharing_platform")
                .setExpiration(Date.from(Instant.now().plusSeconds(60))).signWith(key).compact();
        String missingExpiry = Jwts.builder().setIssuer("video_sharing_platform")
                .claim("username", "alice@example.test").signWith(key).compact();
        for (String token : List.of(expired, wrongIssuer, missingIdentity, missingExpiry)) {
            assertThrows(JwtException.class, () -> provider.parseToken(token));
        }
    }

    @Test
    void rejectsWeakOrInvalidConfiguration() {
        assertThrows(IllegalArgumentException.class, () -> new JwtTokenProvider("short", Duration.ofHours(8)));
        assertThrows(IllegalArgumentException.class, () -> new JwtTokenProvider(newSecret(), Duration.ZERO));
    }
}
