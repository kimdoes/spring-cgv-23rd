package com.ceos23.spring_cgv_23rd.User.Service;

import com.ceos23.spring_cgv_23rd.User.DTO.LoginRequestDTO;
import com.ceos23.spring_cgv_23rd.User.DTO.LoginResponseDTO;
import com.ceos23.spring_cgv_23rd.User.Domain.User;
import com.ceos23.spring_cgv_23rd.User.Repository.UserRepository;
import com.ceos23.spring_cgv_23rd.global.TokenProvider;
import com.ceos23.spring_cgv_23rd.global.JWTType;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
public class LoginService {
    private final PasswordEncoder encoder;
    private final UserRepository userRepository;
    private final TokenProvider jwtProvider;
    private final UserDetailsService userDetailsService;

    public LoginService(PasswordEncoder encoder,
                        UserRepository userRepository,
                        TokenProvider jwtProvider,
                        UserDetailsService userDetailsService){
        this.encoder = encoder;
        this.userRepository = userRepository;
        this.jwtProvider = jwtProvider;
        this.userDetailsService = userDetailsService;
    }

    @Transactional(readOnly = true)
    public LoginResponseDTO login(LoginRequestDTO req){

        User user = userRepository.findByLoginId(req.loginId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "아이디가 올바르지 않습니다."));

        if (!encoder.matches(req.password(), user.getPassword())){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "비밀번호가 올바르지 않습니다.");
        }

        UserDetails userDetails = userDetailsService.loadUserByUsername(user.getUsername());
        Authentication authentication = new UsernamePasswordAuthenticationToken(
                userDetails,
                null,
                userDetails.getAuthorities()
        );

        String jwtAccessToken = jwtProvider.createToken(user.getLoginId(), authentication, JWTType.ACCESS);
        String jwtRefreshToken = jwtProvider.createToken(user.getLoginId(), authentication, JWTType.REFRESH);

        SecurityContextHolder.getContext().setAuthentication(authentication);

        System.out.println("actkn >>> " + jwtAccessToken);
        return LoginResponseDTO.create(jwtAccessToken, jwtRefreshToken);
    }
}
