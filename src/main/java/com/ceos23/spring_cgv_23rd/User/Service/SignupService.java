package com.ceos23.spring_cgv_23rd.User.Service;

import com.ceos23.spring_cgv_23rd.User.DTO.ROLE;
import com.ceos23.spring_cgv_23rd.User.DTO.SignupRequestDTO;
import com.ceos23.spring_cgv_23rd.User.DTO.UserWrapperDTO;
import com.ceos23.spring_cgv_23rd.User.Domain.User;
import com.ceos23.spring_cgv_23rd.User.Repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
public class SignupService {
    private final PasswordEncoder encoder;
    private final UserRepository userRepository;

    public SignupService(PasswordEncoder encoder,
                         UserRepository userRepository){
        this.encoder = encoder;
        this.userRepository = userRepository;
    }

    @Transactional
    public UserWrapperDTO signup(SignupRequestDTO req){
        String encryptedPassword = encoder.encode(req.password());

        passwordValidation(req.password());

        if (userRepository.existsByUsername(req.username())){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "사용자 이름이 이미 존재합니다.");
        }

        if (userRepository.existsByLoginId(req.loginId())){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "중복된 아이디입니다.");
        }

        User user = User.create(
                req.loginId(), req.username(), encryptedPassword, req.men(), req.age()
        );

        return UserWrapperDTO.create(userRepository.save(user));
    }

    private void passwordValidation(String password){
        if (password.length() < 8){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "비밀번호는 반드시 8자 이상이어야합니다.");
        }
        if (!password.contains("*") && !password.contains("!")){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "비밀번호는 반드시 * 또는 !을 포함해야합니다.");
        }
    }
}
