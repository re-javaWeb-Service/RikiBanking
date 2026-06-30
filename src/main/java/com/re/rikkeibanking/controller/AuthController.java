package com.re.rikkeibanking.controller;

import com.re.rikkeibanking.dto.request.LoginRequest;
import com.re.rikkeibanking.dto.request.LogoutRequest;
import com.re.rikkeibanking.dto.request.RefreshTokenRequest;
import com.re.rikkeibanking.dto.response.LoginResponse;
import com.re.rikkeibanking.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class AuthController {
    private final AuthService service;

    @PostMapping("/login")
    public LoginResponse loginResponse(@Valid @RequestBody LoginRequest request){
        return service.login(request);
    }

    @PostMapping("/logout")
    public String logout(@RequestHeader("Authorization") String authorizationHeader,
                         @Valid @RequestBody LogoutRequest request) {
        service.logOut(authorizationHeader,request.getRefreshToken());
        return "Logout successful";
    }

    @PostMapping("/refresh")
    public LoginResponse refresh(@Valid @RequestBody RefreshTokenRequest request){
        return service.refreshToken(request.getRefreshToken());
    }
}
