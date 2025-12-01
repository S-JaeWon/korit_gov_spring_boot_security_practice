package com.korit.security_practice.mapper;

import com.korit.security_practice.entity.OAuth2User;
import org.apache.ibatis.annotations.Mapper;

import java.util.Optional;

@Mapper
public interface OAuth2UserMapper {
    Optional<OAuth2User> getOAuth2UserByProviderAndProviderUserId(String provider, String providerUserId);
    int addOAuth2User(OAuth2User oAuth2User);
}
