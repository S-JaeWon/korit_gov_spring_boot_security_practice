package com.korit.security_practice.service;

import com.korit.security_practice.dto.Request.SendMailReqDto;
import com.korit.security_practice.dto.Response.ApiRespDto;
import com.korit.security_practice.entity.User;
import com.korit.security_practice.entity.UserRole;
import com.korit.security_practice.repository.UserRepository;
import com.korit.security_practice.repository.UserRoleRepository;
import com.korit.security_practice.security.jwt.JwtUtils;
import com.korit.security_practice.security.model.Principal;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class MailService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserRoleRepository userRoleRepository;

    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    private JavaMailSender javaMailSender;

    public ApiRespDto<?> sendMail(SendMailReqDto sendMailReqDto, Principal principal) {
        if (!sendMailReqDto.getEmail().equals(principal.getEmail())) {
            return new ApiRespDto<>("failed", "잘못된 접근 입니다.", null);
        }

        Optional<User> foundUser = userRepository.getUserByEmail(sendMailReqDto.getEmail());

        if (foundUser.isEmpty()) {
            return new ApiRespDto<>("failed", "사용자 정보를 다시 확인해주세요.", null);
        }

        User user = foundUser.get();

        boolean hasTempRole = user.getUserRoles().stream()
                .anyMatch(userRole -> userRole.getRoleId() == 3);

        if (!hasTempRole) {
            return new ApiRespDto<>("failed", "이미 인증된 회원입니다.", null);
        }

        String token = jwtUtils.generateVerifyToken(user.getUserId().toString());

        SimpleMailMessage message = new SimpleMailMessage();

        message.setTo(sendMailReqDto.getEmail());
        message.setSubject("이메일 인증 입니다.");
        message.setText("링크를 클릭해 인증을 완료해 주세요." +
                "http://localhost:8080/mail/verify?verifyToken=" + token);

        javaMailSender.send(message);
        return new ApiRespDto<>("success", "인증 메일이 전송되었습니다.", null);
    }

    public Map<String, Object> verify(String token) {
        Claims claims = null;
        Map<String, Object> resultMap = null;

        try {
            claims = jwtUtils.getClaims(token);
            String subject = claims.getSubject();

            if (!"VerifyToken".equals(subject)) {
                resultMap = Map.of(
                        "status", "failed",
                        "message", "잘못된 접근입니다."
                );
            }

            Integer userId = Integer.parseInt(claims.getId());
            Optional<User> foundUser = userRepository.getUserByUserId(userId);
            if (foundUser.isEmpty()) {
                return Map.of(
                        "status", "failed",
                        "message", "존재하지 않은 사용자"
                );
            }

            List<UserRole> userRoles = foundUser.get().getUserRoles();

            Optional<UserRole> tempUserRole = userRoles.stream()
                    .filter(userRole -> userRole.getRoleId() == 3)
                    .findFirst();

            if (tempUserRole.isEmpty()) {
                return Map.of(
                        "status", "failed",
                        "message", "인증된 사용자"
                );
            }
            UserRole userRole = tempUserRole.get();
            userRole.setRoleId(2);
            userRoleRepository.updateUserRole(userRole);

            return Map.of(
                    "status", "success",
                    "message", "이메일 인증 완료"
            );
        } catch (ExpiredJwtException e) {
            resultMap =  Map.of(
                    "status", "failed",
                    "message", "인증 시간이 만료 되었습니다. \n 인증 메일을 다시 요청해주세요."
            );
        } catch (JwtException e) {
            resultMap =  Map.of(
                    "status", "failed",
                    "message", "오류 발생 \n 인증 메일을 다시 요청해주세요."
            );
        }
        return resultMap;
    }
}
