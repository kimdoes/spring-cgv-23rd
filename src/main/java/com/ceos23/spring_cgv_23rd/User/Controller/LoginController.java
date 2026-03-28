package com.ceos23.spring_cgv_23rd.User.Controller;

import com.ceos23.spring_cgv_23rd.User.DTO.LoginRequestDTO;
import com.ceos23.spring_cgv_23rd.User.DTO.LoginResponseDTO;
import com.ceos23.spring_cgv_23rd.User.Service.LoginService;
import com.ceos23.spring_cgv_23rd.global.JWTType;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/login")
public class LoginController {
    private final LoginService loginService;

    public LoginController(LoginService loginService){
        this.loginService = loginService;
    }

    @PostMapping
    public ResponseEntity<LoginResponseDTO> login(
            @RequestBody LoginRequestDTO req,
            HttpServletResponse res
    ){

        LoginResponseDTO loginRes = loginService.login(req);

        String at = loginRes.accessToken();
        String rt = loginRes.refreshToken();

        Cookie cookie = new Cookie("accessToken", at);
        cookie.setMaxAge(JWTType.ACCESS.getValidTime());
        cookie.setPath("/");
        cookie.setHttpOnly(true);
        cookie.setAttribute("SameSite", "Strict");
        //cookie.setSecure(true); TODO: localhost 환경을 벗어나면 주석제거

        Cookie cookie2 = new Cookie("refreshToken", rt);
        cookie2.setMaxAge(JWTType.REFRESH.getValidTime());
        cookie2.setPath("/");
        cookie2.setHttpOnly(true);
        cookie2.setAttribute("SameSite", "Strict");
        //cookie2.setSecure(true); TODO: localhost 환경을 벗어나면 주석제거

        res.addCookie(cookie);
        res.addCookie(cookie2);

        System.out.println("actkn >>> " + at);
        return ResponseEntity.ok(loginRes);

    }

}
