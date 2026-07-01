package com.re.rikkeibanking.controller;

import com.re.rikkeibanking.dto.request.ForgotPasswordRequest;
import com.re.rikkeibanking.dto.request.LoginRequest;
import com.re.rikkeibanking.dto.request.LogoutRequest;
import com.re.rikkeibanking.dto.request.RefreshTokenRequest;
import com.re.rikkeibanking.dto.request.RegisterRequest;
import com.re.rikkeibanking.dto.request.ResetPasswordRequest;
import com.re.rikkeibanking.dto.response.ApiResponse;
import com.re.rikkeibanking.dto.response.ForgotPasswordResponse;
import com.re.rikkeibanking.dto.response.LoginResponse;
import com.re.rikkeibanking.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class AuthController {
    private final AuthService service;

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<LoginResponse>> login(@Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(ApiResponse.ok("Login successful", service.login(request)));
    }

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<Void>> register(@Valid @RequestBody RegisterRequest request) {
        service.register(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.created("User registered successfully", null));
    }

    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<Void>> logout(
            @RequestHeader("Authorization") String authorizationHeader,
            @Valid @RequestBody LogoutRequest request) {
        service.logOut(authorizationHeader, request.getRefreshToken());
        return ResponseEntity.ok(ApiResponse.message("Logout successful"));
    }

    @PostMapping("/refresh")
    public ResponseEntity<ApiResponse<LoginResponse>> refresh(@Valid @RequestBody RefreshTokenRequest request) {
        return ResponseEntity.ok(ApiResponse.ok(service.refreshToken(request.getRefreshToken())));
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<ApiResponse<ForgotPasswordResponse>> forgotPassword(@Valid @RequestBody ForgotPasswordRequest request) {
        return ResponseEntity.ok(ApiResponse.ok(service.forgotPassword(request)));
    }

    @PostMapping("/reset-password")
    public ResponseEntity<ApiResponse<Void>> resetPassword(@Valid @RequestBody ResetPasswordRequest request) {
        service.resetPassword(request);
        return ResponseEntity.ok(ApiResponse.message("Password reset successfully"));
    }
}
