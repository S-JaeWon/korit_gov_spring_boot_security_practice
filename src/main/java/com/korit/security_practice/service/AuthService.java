package com.korit.security_practice.service;

import com.korit.security_practice.dto.Request.SigninReqDto;
import com.korit.security_practice.dto.Request.SignupReqDto;
import com.korit.security_practice.dto.Response.ApiRespDto;
import com.korit.security_practice.entity.User;
import com.korit.security_practice.entity.UserRole;
import com.korit.security_practice.repository.UserRepository;
import com.korit.security_practice.repository.UserRoleRepository;
import com.korit.security_practice.security.jwt.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AuthService {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private UserRoleRepository userRoleRepository;
    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;
    @Autowired
    private JwtUtils jwtUtils;

    public ApiRespDto<?> signup(SignupReqDto signupReqDto) {
        Optional<User> foundEmail = userRepository.getUserByEmail(signupReqDto.getEmail());
        if (foundEmail.isPresent()) {
            return new ApiRespDto<>("failed", "이미 존재하는 이메일 입니다.", null);
        }
        Optional<User> foundUsername = userRepository.getUserByUsername(signupReqDto.getUsername());
        if (foundUsername.isPresent()) {
            return new ApiRespDto<>("failed", "이미 존재하는 username 입니다.", null);
        }
        Optional<User> foundUser = userRepository.getUserByEmail(signupReqDto.getUsername());
        if (foundUser.isPresent()) {
            return new ApiRespDto<>("failed", "이미 존재하는 username 입니다.", null);
        }

        Optional<User> addUser = userRepository.addUser(signupReqDto.toEntity(bCryptPasswordEncoder));
        UserRole userRole = UserRole.builder()
                .userId(addUser.get().getUserId())
                .roleId(3)
                .build();
        userRoleRepository.addRoleUser(userRole);

        return new ApiRespDto<>("success", "회원가입 성공", addUser.get());
    }

    public ApiRespDto<?> signin(SigninReqDto signinReqDto) {
        Optional<User> foundEmail = userRepository.getUserByEmail(signinReqDto.getEmail());
        if (foundEmail.isEmpty()) {
            return new ApiRespDto<>("failed", "이메일 및 비밀번호를 다시 입력해주세요.", null);
        }
        User user = foundEmail.get();

        if (!bCryptPasswordEncoder.matches(signinReqDto.getPassword(), user.getPassword())) {
            return new ApiRespDto<>("failed", "이메일 및 비밀번호를 다시 확인해주세요.", null);
        }

        String token = jwtUtils.generateAccessToken(user.getUserId().toString());

        return new ApiRespDto<>("success", "로그인 성공", token);
    }
}
