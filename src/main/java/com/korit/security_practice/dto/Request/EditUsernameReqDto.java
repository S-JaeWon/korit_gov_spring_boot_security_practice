package com.korit.security_practice.dto.Request;

import com.korit.security_practice.entity.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EditUsernameReqDto {
    private Integer userId;
    private String oldUsername;
    private String newUsername;

    public User toEntity() {
        return User.builder()
                .userId(userId)
                .username(newUsername)
                .build();
    }
}
