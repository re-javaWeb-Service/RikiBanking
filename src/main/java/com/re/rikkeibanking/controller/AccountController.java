package com.re.rikkeibanking.controller;

import com.re.rikkeibanking.dto.request.AccountStatusRequest;
import com.re.rikkeibanking.dto.request.CreateAccountRequest;
import com.re.rikkeibanking.dto.response.AccountResponseDto;
import com.re.rikkeibanking.dto.response.BalanceResponseDto;
import com.re.rikkeibanking.service.AccountService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
    public Page<AccountResponseDto> getAccounts(
            @RequestParam(required = false) Long userId,
            Pageable pageable,
            Authentication authentication
    ) {
        return accountService.getAccounts(userId, pageable, authentication);
    }

    @GetMapping("/{id}")
    public AccountResponseDto getAccountById(@PathVariable Long id, Authentication authentication) {
        return accountService.getAccountById(id, authentication);
    }

    @GetMapping("/{id}/balance")
    public BalanceResponseDto getBalance(@PathVariable Long id, Authentication authentication) {
        return accountService.getBalance(id, authentication);
    }

    @PostMapping
    public AccountResponseDto createAccount(@Valid @RequestBody CreateAccountRequest request) {
        return accountService.createAccount(request);
    }

    @PatchMapping("/{id}/status")
    public AccountResponseDto updateStatus(
            @PathVariable Long id,
            @Valid @RequestBody AccountStatusRequest request
    ) {
        return accountService.updateStatus(id, request);
    }
}
