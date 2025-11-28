package com.korit.security_practice.dto.Request;

import com.korit.security_practice.entity.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EditPasswordReqDto {
    private Integer userId;
    private String oldPassword;
    private String newPassword;

    public User toEntity(BCryptPasswordEncoder bCryptPasswordEncoder) {
        return User.builder()
                .userId(userId)
                .password(bCryptPasswordEncoder.encode(newPassword))
                .build();
    }
}
