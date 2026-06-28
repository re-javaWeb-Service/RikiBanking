package com.re.rikkeibanking.controller;

import com.re.rikkeibanking.dto.request.LoginRequest;
import com.re.rikkeibanking.dto.response.LoginResponse;
import com.re.rikkeibanking.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class AuthController {
    private final AuthService service;

    @PostMapping("/login")
    public LoginResponse loginResponse(@Valid @RequestBody LoginRequest request){
        return service.login(request);
    }
}
