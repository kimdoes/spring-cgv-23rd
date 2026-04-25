package com.ceos23.spring_cgv_23rd.global.Filter;

import com.ceos23.spring_cgv_23rd.Token.Domain.RefreshToken;
import com.ceos23.spring_cgv_23rd.Token.Repository.TokenRepository;
import com.ceos23.spring_cgv_23rd.Token.Service.TokenProvider;
import com.ceos23.spring_cgv_23rd.User.Domain.User;
import com.ceos23.spring_cgv_23rd.global.Exception.CustomException;
import com.ceos23.spring_cgv_23rd.global.Exception.ErrorCode;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomLogoutFilter implements LogoutHandler {
    private final TokenProvider tokenProvider;
    private final TokenRepository tokenRepository;

    @Override
    public void logout(@NonNull HttpServletRequest request,
                       @NonNull HttpServletResponse response,
                       @Nullable Authentication authentication) {
        SecurityContextHolder.clearContext();

        String token = tokenProvider.getRefreshToken(request);
        RefreshToken refreshToken = tokenRepository.findByToken(token).orElseThrow(
                () -> new CustomException(ErrorCode.NOT_FOUND_REFRESH_TOKEN)
        );

        Cookie cookie = new Cookie("refreshToken", null);
        cookie.setMaxAge(0);
        cookie.setPath("/");
        response.addCookie(cookie);

        refreshToken.revoke();
    }
}
