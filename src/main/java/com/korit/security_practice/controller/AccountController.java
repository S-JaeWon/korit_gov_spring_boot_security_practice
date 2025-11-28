package com.korit.security_practice.controller;

import com.korit.security_practice.dto.Request.EditPasswordReqDto;
import com.korit.security_practice.dto.Request.EditUsernameReqDto;
import com.korit.security_practice.security.model.Principal;
import com.korit.security_practice.service.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/account")
public class AccountController {

    @Autowired
    private AccountService accountService;

    @PostMapping("/edit/password")
    public ResponseEntity<?> editPassword(
            @RequestBody EditPasswordReqDto editPasswordReqDto,
            @AuthenticationPrincipal Principal principal
    ) {
        return ResponseEntity.ok(accountService.editPassword(editPasswordReqDto, principal));
    }

    @PostMapping("/edit/username")
    public ResponseEntity<?> editUsername(
            @RequestBody EditUsernameReqDto editUsernameReqDto,
            @AuthenticationPrincipal Principal principal
    ) {
        return ResponseEntity.ok(accountService.editUsername(editUsernameReqDto, principal));
    }
}
