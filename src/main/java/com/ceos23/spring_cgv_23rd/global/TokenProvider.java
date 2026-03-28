package com.ceos23.spring_cgv_23rd.global;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.io.Encoders;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

import javax.crypto.SecretKey;
import java.security.Key;
import java.util.*;
import java.util.stream.Collectors;

@Component
public class TokenProvider implements InitializingBean {
    private Key key;
    private final UserDetailsService userDetailsService;
    @Value("${jwt.secret-key}")
    private String jwtSecretKey;

    public TokenProvider(UserDetailsService userDetailsService){
        this.userDetailsService = userDetailsService;
    }

    @Override
    public void afterPropertiesSet() {
        byte[] keyBytes = Decoders.BASE64.decode(jwtSecretKey);
        this.key = Keys.hmacShaKeyFor(keyBytes);
    }

    public String getAccessToken(HttpServletRequest req) {
        Cookie[] cookies = req.getCookies();

        if (cookies == null) return null;

        for (Cookie c : cookies) {
            if ("accessToken".equals(c.getName())) {
                return c.getValue();
            }
        }

        return null;
    }

    public String createToken(String loginId, Authentication authentication, JWTType type){
        String authorities =
                authentication.getAuthorities().stream()
                        .map(GrantedAuthority::getAuthority)
                        .collect(Collectors.joining(","));

        Claims claims = Jwts.claims().build();
        int expiration = type.getValidTime() * 1000;

        claims.put("auth", authorities);

        return Jwts.builder()
                .subject(loginId)
                .expiration(new Date(new Date().getTime() + expiration))
                .claims(claims)
                .issuedAt(new Date())
                .signWith(key)
                .compact();
    }

    public String getTokenLoginId(String token) {
        return Jwts.parser().verifyWith((SecretKey) key).build().parseSignedClaims(token).getPayload().getSubject();
    }

    public Authentication getAuthentication(String token) {
        UserDetails userDetails =
                (UserDetails)
                        userDetailsService.loadUserByUsername(getTokenLoginId(token));

        return new UsernamePasswordAuthenticationToken(
                userDetails, token, userDetails.getAuthorities());
    }
}
