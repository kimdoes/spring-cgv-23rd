package com.ceos23.spring_cgv_23rd.global;

import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;

public class JWTAuthenticationFilter extends OncePerRequestFilter {
    private final TokenProvider tokenProvider;

    public JWTAuthenticationFilter(TokenProvider tokenProvider){
        this.tokenProvider = tokenProvider;
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest req, HttpServletResponse res, FilterChain filterChain)
            throws ServletException, IOException {
        System.out.println("진입!!!");
        System.out.println("DISPATCH: " + req.getDispatcherType());
        System.out.println("URI: " + req.getRequestURI());

        try {
            if (SecurityContextHolder.getContext().getAuthentication() != null){
                System.out.println("컨텍스트에 무언가 있음!!!");
                filterChain.doFilter(req, res);
                return;
            }

            String at = tokenProvider.getAccessToken(req);

            if (at == null) {
                System.out.println("토큰이 없음!!!");
                filterChain.doFilter(req, res);
                return;
            }

            Authentication authentication = tokenProvider.getAuthentication(at);
            SecurityContextHolder.getContext().setAuthentication(authentication);
            System.out.println("통과!!!");

        } catch (JwtException e){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "JWT 토큰이 만료되었습니다.");
        }

        filterChain.doFilter(req, res);
    }
}
