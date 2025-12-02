package com.korit.security_practice.service;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Objects;


@Service
public class OAuth2PrincipalService extends DefaultOAuth2UserService {

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        // Spring Security가 OAuth2 provider에게 AccessToken(플랫폼에게 받아온 것)으로 사용자 정보 요청
        // 그 결과로 받은 사용자 정보(JSON)를 파싱한 객체 반환
        OAuth2User oAuth2User = super.loadUser(userRequest);

        // 사용자 정보 map 형태로 추출
        Map<String, Object> attributes = oAuth2User.getAttributes();

        // 어떤 provider(플랫폼)인지 확인
        String provider = userRequest.getClientRegistration().getRegistrationId();

        String email = null;
        // 사용자 식별자
        String providerUserId = null;
        switch (provider) {
            case "google":
                providerUserId = attributes.get("sub").toString();
                email = (String) attributes.get("email");
                break;
            case "naver":
                Map<String, Object> response = (Map<String, Object>) attributes.get("response");
                providerUserId = response.get("id").toString();
                email = (String) response.get("email");
                break;
            case "kakao":
                providerUserId = attributes.get("id").toString();
                Map<String, Object> kakaoAccount = (Map<String, Object>) attributes.get("kakao_account");
                email = (String) kakaoAccount.get("email");
                break;
        }

        Map<String, Object> newAttributes = Map.of(
                "providerUserId", providerUserId,
                "provider", provider,
                "email", email
        );

        // 임시 권한 부여 (ROLE_TEMP)
        // 실제 권한은 OAuth2SuccessHandler에서 판단
        List<SimpleGrantedAuthority> authorities = List.of(new SimpleGrantedAuthority("ROLE_TEMP"));

        // Spring Security가 사용할 OAuth2User 객체 생성해서 반환
        // id -> principal.getName() 했을 때 사용할 이름
        return new DefaultOAuth2User(authorities, newAttributes, "providerUserId");
    }
}
