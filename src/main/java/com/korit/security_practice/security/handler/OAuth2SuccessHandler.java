package com.korit.security_practice.security.handler;

import com.korit.security_practice.entity.OAuth2User;
import com.korit.security_practice.entity.User;
import com.korit.security_practice.repository.OAuth2UserRepository;
import com.korit.security_practice.repository.UserRepository;
import com.korit.security_practice.security.jwt.JwtUtils;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Optional;

@Component
public class OAuth2SuccessHandler implements AuthenticationSuccessHandler {

    @Autowired
    private OAuth2UserRepository oAuth2UserRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtUtils jwtUtils;

    @Override
    public void onAuthenticationSuccess(
            HttpServletRequest request,
            HttpServletResponse response,
            Authentication authentication
    ) throws IOException, ServletException {
        // OAuth2User 정보를 파싱
        DefaultOAuth2User defaultOAuth2User = (DefaultOAuth2User) authentication.getPrincipal();
        String provider = defaultOAuth2User.getAttribute("provider"); // 플랫폼 이름 ex) 카카오, 구글, 네이버 등등
        String providerUserId = defaultOAuth2User.getAttribute("providerUserId"); // 식별 id
        String email = defaultOAuth2User.getAttribute("email");
        System.out.println("------------------------------------");
        System.out.println("provider: " + provider);
        System.out.println("providerUserId: " + providerUserId);
        System.out.println("email: " + email);
        System.out.println("------------------------------------");

        // provider, providerUserId로 이미 연동된 사용자 정보 있는지 확인
        Optional<OAuth2User> foundOAuth2User = oAuth2UserRepository.getOAuth2UserByProviderAndProviderUserId(provider, providerUserId);

        // OAuth2를 통해 회원가입 x or 연동 x
        if (foundOAuth2User.isEmpty()) {
            response.sendRedirect("http://localhost:3000/auth/oauth2?provider=" + provider + "&providerUserId=" + providerUserId + "&email=" + email);
            return;
        }

        // 연동된 사용자가 있다면 -> userId를 통해 회원 정보 조회
        Optional<User> foundUser = userRepository.getUserByUserId(foundOAuth2User.get().getOauth2Id());
        String accessToken = null;
        if (foundUser.isPresent()) {
            accessToken = jwtUtils.generateAccessToken(Integer.toString(foundUser.get().getUserId()));
        }

        response.sendRedirect("http://localhost:3000/auth/oauth2/signin?accessToken=" + accessToken);
    }
}
