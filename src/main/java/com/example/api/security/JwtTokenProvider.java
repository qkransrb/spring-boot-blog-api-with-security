package com.example.api.security;

import com.example.api.exception.BlogAPIException;
import io.jsonwebtoken.*;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.Date;

@Component
@RequiredArgsConstructor
public class JwtTokenProvider {

    @Value("${app.jwt-secret}")
    private String jwtSecret;

    @Value("${app.jwt-expiration-milliseconds}")
    private int jwtExpirationMilliseconds;

    public String generateToken(Authentication authentication) {
        String username = authentication.getName();

        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(new Date())
                .setExpiration(new Date(new Date().getTime() + jwtExpirationMilliseconds))
                .signWith(SignatureAlgorithm.HS512, jwtSecret)
                .compact();
    }

    public String getUsernameFromToken(String token) {
        return Jwts.parser()
                .setSigningKey(jwtSecret)
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

    public boolean validateToken(String token) {
        try {
            if (!StringUtils.hasText(getUsernameFromToken(token))) {
                throw new BlogAPIException(HttpStatus.UNAUTHORIZED, "Invalid JWT Token");
            }
            return true;
        } catch (SignatureException e) {
            throw new BlogAPIException(HttpStatus.UNAUTHORIZED, "Invalid JWT Signature");
        } catch (MalformedJwtException e) {
            throw new BlogAPIException(HttpStatus.UNAUTHORIZED, "Invalid JWT Token");
        } catch (ExpiredJwtException e) {
            throw new BlogAPIException(HttpStatus.UNAUTHORIZED, "Expired JWT Token");
        } catch (UnsupportedJwtException e) {
            throw new BlogAPIException(HttpStatus.UNAUTHORIZED, "Unsupported JWT Signature");
        } catch (IllegalArgumentException e) {
            throw new BlogAPIException(HttpStatus.UNAUTHORIZED, "JWT claims string is empty");
        }
    }
}
