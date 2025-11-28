package com.korit.security_practice.security.filter;

import com.korit.security_practice.entity.User;
import com.korit.security_practice.repository.UserRepository;
import com.korit.security_practice.security.jwt.JwtUtils;
import com.korit.security_practice.security.model.Principal;
import io.jsonwebtoken.Claims;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;
import java.util.Optional;


@Component
public class JwtAuthenticationFilter implements Filter {

    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    private UserRepository userRepository;

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;

        List<String> methods = List.of("POST", "PUT", "GET", "PATCH", "DELETE");
        if (!methods.contains(request.getMethod())) {
            filterChain.doFilter(servletRequest, servletResponse);
            return;
        }

        String authorization = request.getHeader("Authorization");
        System.out.println("토큰: " + authorization);

        if (jwtUtils.isBearer(authorization)) {
            String accessToken = jwtUtils.removeBearer(authorization);
            try {
                Claims claims = jwtUtils.getClaims(accessToken);
                String id = claims.getId();
                Integer userId = Integer.parseInt(id);
                Optional<User> foundUser = userRepository.getUserByUserId(userId);
                foundUser.ifPresentOrElse((user) -> {
                    Principal principal = Principal.builder()
                            .userId(user.getUserId())
                            .username(user.getUsername())
                            .password(user.getPassword())
                            .email(user.getEmail())
                            .userRoles(user.getUserRoles())
                            .build();
                    Authentication authentication = new UsernamePasswordAuthenticationToken(principal, "", principal.getAuthorities());
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                }, () -> {
                    throw new AuthenticationServiceException("인증 실패");
                });
            } catch (RuntimeException e) {
                e.printStackTrace();
            }
        }


        filterChain.doFilter(servletRequest, servletResponse);
    }
}
