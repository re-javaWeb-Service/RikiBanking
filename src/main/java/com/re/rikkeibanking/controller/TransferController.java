package com.re.rikkeibanking.controller;

import com.re.rikkeibanking.dto.request.TransferRequest;
import com.re.rikkeibanking.dto.response.ApiResponse;
import com.re.rikkeibanking.dto.response.TransferResponse;
import com.re.rikkeibanking.service.TransferService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/transactions")
@RequiredArgsConstructor
public class TransferController {

    private final TransferService transferService;

    @PostMapping("/transfer")
    public ResponseEntity<ApiResponse<TransferResponse>> transfer(
            @Valid @RequestBody TransferRequest request, Authentication authentication) {
        return ResponseEntity.ok(ApiResponse.ok("Transfer successful", transferService.transfer(request, authentication)));
    }
}
