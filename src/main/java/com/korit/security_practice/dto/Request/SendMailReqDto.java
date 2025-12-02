package com.korit.security_practice.dto.Request;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class SendMailReqDto {
    private String email;
}
