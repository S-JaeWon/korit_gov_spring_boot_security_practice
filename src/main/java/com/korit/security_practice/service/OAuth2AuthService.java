package com.korit.security_practice.service;

import com.korit.security_practice.dto.Request.OAuth2SignupReqDto;
import com.korit.security_practice.dto.Response.ApiRespDto;
import com.korit.security_practice.entity.User;
import com.korit.security_practice.entity.UserRole;
import com.korit.security_practice.repository.OAuth2UserRepository;
import com.korit.security_practice.repository.UserRepository;
import com.korit.security_practice.repository.UserRoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

// oauth2로 회원가입 또는 기존 계정 연동
@Service
public class OAuth2AuthService {

    @Autowired
    private OAuth2UserRepository oAuth2UserRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private UserRoleRepository userRoleRepository;
    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    public ApiRespDto<?> signup(OAuth2SignupReqDto oAuth2SignupReqDto) {
        Optional<User> foundUser = userRepository.getUserByEmail(oAuth2SignupReqDto.getEmail());

        if (foundUser.isPresent()) {
            return new ApiRespDto<>("failed", "이미 존재하는 이메일 입니다.", null);
        }

        Optional<User> foundUserByUsername = userRepository.getUserByUsername(oAuth2SignupReqDto.getUsername());

        if (foundUserByUsername.isPresent()) {
            return new ApiRespDto<>("failed", "이미지 존재하는 username 입니다.", null);
        }

        Optional<User> user = userRepository.addUser(oAuth2SignupReqDto.toUserEntity(bCryptPasswordEncoder));
        UserRole userRole = UserRole.builder()
                .userId(user.get().getUserId())
                .roleId(3)
                .build();
        userRoleRepository.addRoleUser(userRole);
        oAuth2UserRepository.addOAuth2User(oAuth2SignupReqDto.toOAuth2UserEntity(user.get().getUserId()));

        return new ApiRespDto<>("success", oAuth2SignupReqDto.getProvider() + " 로 회원가입 완료", null);
    }


}
