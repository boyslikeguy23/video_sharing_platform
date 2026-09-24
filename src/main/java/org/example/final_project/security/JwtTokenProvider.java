package org.example.final_project.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.example.final_project.models.User;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.time.Duration;
import java.time.Instant;
import java.util.Date;
import java.util.stream.Collectors;

@Service
public class JwtTokenProvider {
    private static final String ISSUER = "video_sharing_platform";
    private final SecretKey key;
    private final Duration ttl;

    public JwtTokenProvider(@Value("${app.jwt.secret}") String secret,
                            @Value("${app.jwt.ttl:PT8H}") Duration ttl) {
        try {
            this.key = Keys.hmacShaKeyFor(Decoders.BASE64.decode(secret));
        } catch (RuntimeException ex) {
            throw new IllegalArgumentException("JWT_SECRET must be Base64 encoding of at least 32 random bytes");
        }
        if (ttl.isNegative() || ttl.isZero()) {
            throw new IllegalArgumentException("JWT_TTL must be positive");
        }
        this.ttl = ttl;
    }

    public Claims parseToken(String token) {
        Claims claims = Jwts.parserBuilder().setSigningKey(key).requireIssuer(ISSUER)
                .build().parseClaimsJws(token).getBody();
        String username = claims.get("username", String.class);
        if (username == null || username.isBlank() || claims.getExpiration() == null) {
            throw new JwtException("Token is missing required claims");
        }
        return claims;
    }

    public JwtTokenClaims getClaimsFromToken(String token) {
        JwtTokenClaims result = new JwtTokenClaims();
        result.setUsername(parseToken(token).get("username", String.class));
        return result;
    }

    public String generateJwtToken(Authentication authentication) {
        String authorities = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority).collect(Collectors.joining(","));
        return createToken(authentication.getName(), authorities);
    }

    public String generateJwtToken(User user) {
        return createToken(user.getEmail(), "");
    }

    private String createToken(String username, String authorities) {
        Instant now = Instant.now();
        return Jwts.builder().setIssuer(ISSUER)
                .claim("username", username).claim("authorities", authorities)
                .setIssuedAt(Date.from(now)).setExpiration(Date.from(now.plus(ttl)))
                .signWith(key).compact();
    }
}
