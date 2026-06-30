package com.re.rikkeibanking.service;

import com.re.rikkeibanking.dto.request.AccountStatusRequest;
import com.re.rikkeibanking.dto.request.CreateAccountRequest;
import com.re.rikkeibanking.dto.response.AccountResponseDto;
import com.re.rikkeibanking.dto.response.BalanceResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;

public interface AccountService {
    Page<AccountResponseDto> getAccounts(Long userId, Pageable pageable, Authentication authentication);

    AccountResponseDto getAccountById(Long id, Authentication authentication);

    BalanceResponseDto getBalance(Long id, Authentication authentication);

    AccountResponseDto createAccount(CreateAccountRequest request);

    AccountResponseDto updateStatus(Long id, AccountStatusRequest request);
}
