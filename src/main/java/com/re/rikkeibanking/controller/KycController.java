package com.re.rikkeibanking.controller;

import com.re.rikkeibanking.dto.response.ApiResponse;
import com.re.rikkeibanking.dto.response.KycProfileResponseDto;
import com.re.rikkeibanking.service.KycService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/kyc")
public class KycController {
    private final KycService kycService;

    @PostMapping("/upload")
    public ResponseEntity<ApiResponse<KycProfileResponseDto>> upload(
            Authentication authentication,
            @RequestParam String fullName,
            @RequestParam String idNumber,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dob,
            @RequestParam(required = false) String sex,
            @RequestParam(required = false) String address,
            @RequestParam MultipartFile idCardFront,
            @RequestParam(required = false) MultipartFile idCardBack
    ) {
        KycProfileResponseDto result = kycService.upload(authentication, fullName, idNumber, dob, sex, address, idCardFront, idCardBack);
        return ResponseEntity.status(org.springframework.http.HttpStatus.CREATED)
                .body(ApiResponse.created("KYC profile uploaded successfully", result));
    }

    @GetMapping("/me")
    public ResponseEntity<ApiResponse<KycProfileResponseDto>> getMyProfile(Authentication authentication) {
        return ResponseEntity.ok(ApiResponse.ok(kycService.getMyProfile(authentication)));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<Page<KycProfileResponseDto>>> getProfiles(
            @RequestParam(required = false) String status,
            Pageable pageable
    ) {
        return ResponseEntity.ok(ApiResponse.ok(kycService.getProfiles(status, pageable)));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<KycProfileResponseDto>> getProfile(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.ok(kycService.getProfile(id)));
    }

    @PatchMapping("/{id}/approve")
    public ResponseEntity<ApiResponse<KycProfileResponseDto>> approve(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.ok("KYC profile approved", kycService.approve(id)));
    }

    @PatchMapping("/{id}/reject")
    public ResponseEntity<ApiResponse<KycProfileResponseDto>> reject(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.ok("KYC profile rejected", kycService.reject(id)));
    }
}
