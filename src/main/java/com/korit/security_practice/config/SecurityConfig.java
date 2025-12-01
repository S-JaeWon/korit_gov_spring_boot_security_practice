package com.korit.security_practice.config;

import com.korit.security_practice.security.filter.JwtAuthenticationFilter;
import com.korit.security_practice.security.handler.OAuth2SuccessHandler;
import com.korit.security_practice.service.OAuth2PrincipalService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Slf4j
@Configuration
public class SecurityConfig {
    @Autowired
    private JwtAuthenticationFilter jwtAuthenticationFilter;
    @Autowired
    private OAuth2PrincipalService oAuth2PrincipalService;
    @Autowired
    private OAuth2SuccessHandler oAuth2SuccessHandler;

    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();

        configuration.addAllowedOriginPattern(CorsConfiguration.ALL);
        configuration.addAllowedHeader(CorsConfiguration.ALL);
        configuration.addAllowedMethod(CorsConfiguration.ALL);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.cors(Customizer.withDefaults());
        http.csrf(csrf -> csrf.disable());

        http.formLogin(login -> login.disable());
        http.httpBasic(basic -> basic.disable());
        http.logout(logout -> logout.disable());

        http.sessionManagement(Session -> Session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        http.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        http.authorizeHttpRequests(auth -> {
            auth.requestMatchers("/auth/**", "/login/oauth2/**", "/oauth2/**").permitAll();
            auth.anyRequest().authenticated();
        });

        // 요청이 들어오면 Spring Security의 filterChain을 탄다
        // 여기서 여러 필터 중 하나가 OAuth2 요청 감지
        // 감지되면 해당 provider의 로그인 페이지로 리디렉션
        http.oauth2Login(
                // OAuth2 로그인 요청이 성공하고 사용자 정보를 가져오는 과정 설정
                oauth2 -> oauth2.userInfoEndpoint(
                        // 사용자 정보 요청이 완료가 되면 이 커스텀 서비스로 OAuth2User 객채에 대한 처리를 하겠다고 설정
                        userInfo -> userInfo.userService(oAuth2PrincipalService))
                        // 사용자 정보 파싱이 끝난 후 실행할 핸들러 설정
                        .successHandler(oAuth2SuccessHandler));

        return http.build();
    }
}
