package org.example.final_project.configs;

import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.example.final_project.security.JwtTokenProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

public class JwtValidationFilter extends OncePerRequestFilter {
    private final JwtTokenProvider tokens;

    public JwtValidationFilter(JwtTokenProvider tokens) {
        this.tokens = tokens;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {
        String header = request.getHeader(SecurityContest.HEADER);
        // Basic authentication is used by the existing /signin endpoint.
        if (header != null && !header.regionMatches(true, 0, "Basic ", 0, 6)) {
            try {
                if (!header.regionMatches(true, 0, "Bearer ", 0, 7) || header.substring(7).isBlank()) {
                    throw new IllegalArgumentException("Expected a bearer token");
                }
                Claims claims = tokens.parseToken(header.substring(7));
                String authorities = claims.get("authorities", String.class);
                var authentication = new UsernamePasswordAuthenticationToken(
                        claims.get("username", String.class), null,
                        AuthorityUtils.commaSeparatedStringToAuthorityList(authorities == null ? "" : authorities));
                SecurityContextHolder.getContext().setAuthentication(authentication);
            } catch (RuntimeException ex) {
                SecurityContextHolder.clearContext();
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.setContentType("application/json");
                response.getWriter().write("{\"error\":\"Invalid or expired access token\"}");
                return;
            }
        }
        chain.doFilter(request, response);
    }
}
