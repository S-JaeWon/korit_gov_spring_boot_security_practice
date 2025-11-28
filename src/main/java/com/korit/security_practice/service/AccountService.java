package com.korit.security_practice.service;

import com.korit.security_practice.dto.Request.EditPasswordReqDto;
import com.korit.security_practice.dto.Request.EditUsernameReqDto;
import com.korit.security_practice.dto.Response.ApiRespDto;
import com.korit.security_practice.entity.User;
import com.korit.security_practice.repository.UserRepository;
import com.korit.security_practice.security.model.Principal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AccountService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    public ApiRespDto<?> editPassword(EditPasswordReqDto editPasswordReqDto, Principal principal) {
        if (!editPasswordReqDto.getUserId().equals(principal.getUserId())) {
            return new ApiRespDto<>("failed", "잘못된 접근입니다.", null);
        }

        Optional<User> foundUser = userRepository.getUserByUserId(editPasswordReqDto.getUserId());
        if (foundUser.isEmpty()) {
            return new ApiRespDto<>("failed", "존재하지 않은 사용자입니다.", null);
        }
        if (!bCryptPasswordEncoder.matches(editPasswordReqDto.getOldPassword(), foundUser.get().getPassword())) {
            return new ApiRespDto<>("failed", "기존 비밀번호와 일치하지 않습니다.", null);
        }
        if (bCryptPasswordEncoder.matches(editPasswordReqDto.getNewPassword(), foundUser.get().getPassword())) {
            return new ApiRespDto<>("failed", "이미 사용중인 비밀번호 입니다.", null);
        }

        int result = userRepository.updatePassword(editPasswordReqDto.toEntity(bCryptPasswordEncoder));

        if (result != 1) {
            return new ApiRespDto<>("failed", "오류 발생", null);
        }
        return new ApiRespDto<>("success", "비밀번호 수정 완료", null);
    }

    public ApiRespDto<?> editUsername(EditUsernameReqDto editUsernameReqDto, Principal principal) {
        if (!editUsernameReqDto.getUserId().equals(principal.getUserId())) {
            return new ApiRespDto<>("failed", "잘못된 접근입니다.", null);
        }

        Optional<User> foundUser = userRepository.getUserByUserId(editUsernameReqDto.getUserId());
        if (foundUser.isEmpty()) {
            return new ApiRespDto<>("failed", "존재하지 않은 사용자입니다.", null);
        }
        if (!editUsernameReqDto.getOldUsername().equals(foundUser.get().getUsername())) {
            return new ApiRespDto<>("failed", "기존 username과 일치하지 않습니다.", null);
        }
        if (editUsernameReqDto.getNewUsername().equals(foundUser.get().getUsername())) {
            return new ApiRespDto<>("failed", "사용중인 username 입니다.", null);
        }

        int result = userRepository.updateUsername(editUsernameReqDto.toEntity());

        if (result != 1) {
            return new ApiRespDto<>("failed", "오류 발생", null);
        }
        return new ApiRespDto<>("success", "username 수정 완료", null);
     }
}
