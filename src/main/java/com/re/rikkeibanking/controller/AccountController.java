package com.re.rikkeibanking.controller;

import com.re.rikkeibanking.dto.request.AccountStatusRequest;
import com.re.rikkeibanking.dto.request.ChangePinRequest;
import com.re.rikkeibanking.dto.request.CreateAccountRequest;
import com.re.rikkeibanking.dto.response.AccountResponseDto;
import com.re.rikkeibanking.dto.response.ApiResponse;
import com.re.rikkeibanking.dto.response.BalanceResponseDto;
import com.re.rikkeibanking.dto.response.TransactionStatementDto;
import com.re.rikkeibanking.service.AccountService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/accounts")
public class AccountController {

    private final AccountService accountService;

    @GetMapping
    public ResponseEntity<ApiResponse<Page<AccountResponseDto>>> getAccounts(
            @RequestParam(required = false) Long userId,
            Pageable pageable,
            Authentication authentication
    ) {
        return ResponseEntity.ok(ApiResponse.ok(accountService.getAccounts(userId, pageable, authentication)));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<AccountResponseDto>> getAccountById(
            @PathVariable Long id, Authentication authentication) {
        return ResponseEntity.ok(ApiResponse.ok(accountService.getAccountById(id, authentication)));
    }

    @GetMapping("/{id}/balance")
    public ResponseEntity<ApiResponse<BalanceResponseDto>> getBalance(
            @PathVariable Long id, Authentication authentication) {
        return ResponseEntity.ok(ApiResponse.ok(accountService.getBalance(id, authentication)));
    }

    @GetMapping("/{id}/transactions")
    public ResponseEntity<ApiResponse<Page<TransactionStatementDto>>> getTransactionStatements(
            @PathVariable Long id,
            Pageable pageable,
            Authentication authentication
    ) {
        return ResponseEntity.ok(ApiResponse.ok(accountService.getTransactionStatements(id, pageable, authentication)));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<AccountResponseDto>> createAccount(
            @Valid @RequestBody CreateAccountRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.created("Account created successfully", accountService.createAccount(request)));
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<ApiResponse<AccountResponseDto>> updateStatus(
            @PathVariable Long id,
            @Valid @RequestBody AccountStatusRequest request
    ) {
        return ResponseEntity.ok(ApiResponse.ok("Account status updated", accountService.updateStatus(id, request)));
    }

    @PatchMapping("/{id}/pin")
    public ResponseEntity<ApiResponse<Void>> changePin(
            @PathVariable Long id,
            @Valid @RequestBody ChangePinRequest request,
            Authentication authentication
    ) {
        accountService.changePin(id, request, authentication);
        return ResponseEntity.ok(ApiResponse.message("Transaction PIN changed successfully"));
    }
}
