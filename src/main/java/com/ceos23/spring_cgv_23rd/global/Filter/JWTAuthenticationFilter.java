
package com.ceos23.spring_cgv_23rd.global;

import com.ceos23.spring_cgv_23rd.Token.Service.TokenProvider;
import com.ceos23.spring_cgv_23rd.global.Exception.CustomException;
import com.ceos23.spring_cgv_23rd.global.Exception.ErrorCode;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.security.SignatureException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.jspecify.annotations.NonNull;
import org.springframework.core.type.classreading.ClassFormatException;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
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
            @NonNull HttpServletRequest req, @NonNull HttpServletResponse res, @NonNull FilterChain filterChain)
            throws ServletException, IOException {

        if (SecurityContextHolder.getContext().getAuthentication() != null){
            filterChain.doFilter(req, res);
            return;
        }

        String at = tokenProvider.getAccessToken(req);

        try {
            if (StringUtils.hasText(at)) {
                tokenProvider.validateToken(at);

                Authentication au = tokenProvider.getAuthentication(at);
                SecurityContextHolder.getContext().setAuthentication(au);
            }
        }
        catch (SignatureException sj){
            sj.printStackTrace();
            throw new CustomException(ErrorCode.COUNTERFEIT_SIGNATURE);
        } catch (ExpiredJwtException ej){
            ej.printStackTrace();
            throw new CustomException(ErrorCode.EXPIRED_TOKEN);
        } catch (JwtException je){
            je.printStackTrace();
            throw new CustomException(ErrorCode.INVALID_TOKEN);
        } catch (Exception e){
            e.printStackTrace();
            throw new CustomException(ErrorCode.INTERNAL_SERVER_ERROR);
        }

        filterChain.doFilter(req, res);
    }

}