package com.korit.security_practice.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class User {
    private Integer userId;
    private String email;
    @JsonIgnore
    private String password;
    private String username;
    private LocalDateTime createDt;
    private LocalDateTime updateDt;

    private List<UserRole> userRoles;
}
