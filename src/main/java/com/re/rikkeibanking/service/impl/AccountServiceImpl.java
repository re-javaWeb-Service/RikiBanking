package com.re.rikkeibanking.service.impl;

import com.re.rikkeibanking.dto.request.AccountStatusRequest;
import com.re.rikkeibanking.dto.request.CreateAccountRequest;
import com.re.rikkeibanking.dto.response.AccountResponseDto;
import com.re.rikkeibanking.dto.response.BalanceResponseDto;
import com.re.rikkeibanking.entity.Account;
import com.re.rikkeibanking.entity.User;
import com.re.rikkeibanking.exception.BusinessException;
import com.re.rikkeibanking.map.AccountMapper;
import com.re.rikkeibanking.repository.AccountRepository;
import com.re.rikkeibanking.repository.UserRepository;
import com.re.rikkeibanking.security.UserPrincipal;
import com.re.rikkeibanking.service.AccountService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AccountServiceImpl implements AccountService {

    private final AccountRepository accountRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public Page<AccountResponseDto> getAccounts(Long userId, Pageable pageable, Authentication authentication) {
        Long effectiveUserId = userId;

        if (isCustomer(authentication)) {
            effectiveUserId = currentUserId(authentication);
        }

        Page<Account> accounts = effectiveUserId == null
                ? accountRepository.findAll(pageable)
                : accountRepository.findByUserId(effectiveUserId, pageable);

        return accounts.map(AccountMapper::toResponseDto);
    }

    @Override
    public AccountResponseDto getAccountById(Long id, Authentication authentication) {
        Account account = findAccountForCurrentUser(id, authentication);
        return AccountMapper.toResponseDto(account);
    }

    @Override
    public BalanceResponseDto getBalance(Long id, Authentication authentication) {
        Account account = findAccountForCurrentUser(id, authentication);
        return AccountMapper.toBalanceResponseDto(account);
    }

    @Override
    @Transactional
    public AccountResponseDto createAccount(CreateAccountRequest request) {
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new BusinessException("User not found", HttpStatus.NOT_FOUND));

        if (!Boolean.TRUE.equals(user.getIsKyc())) {
            throw new BusinessException("User must complete KYC before opening account", HttpStatus.BAD_REQUEST);
        }

        Account account = new Account();
        account.setUser(user);
        account.setCurrency(request.getCurrency());
        account.setAccountNumber(generateAccountNumber());
        account.setTransactionPin(passwordEncoder.encode(request.getTransactionPin()));
        account.setActive(true);

        accountRepository.save(account);
        return AccountMapper.toResponseDto(account);
    }

    @Override
    @Transactional
    public AccountResponseDto updateStatus(Long id, AccountStatusRequest request) {
        Account account = findAccountById(id);
        account.setActive(request.getActive());
        accountRepository.save(account);
        return AccountMapper.toResponseDto(account);
    }

    private Account findAccountById(Long id) {
        return accountRepository.findById(id)
                .orElseThrow(() -> new BusinessException("Account not found", HttpStatus.NOT_FOUND));
    }

    private Account findAccountForCurrentUser(Long id, Authentication authentication) {
        Account account = findAccountById(id);
        if (isCustomer(authentication) && !account.getUser().getId().equals(currentUserId(authentication))) {
            throw new BusinessException("You do not have permission to access this account", HttpStatus.FORBIDDEN);
        }
        return account;
    }

    private boolean isCustomer(Authentication authentication) {
        return authentication.getAuthorities().stream()
                .anyMatch(authority -> authority.getAuthority().equals("ROLE_CUSTOMER"));
    }

    private Long currentUserId(Authentication authentication) {
        Object principal = authentication.getPrincipal();
        if (principal instanceof UserPrincipal userPrincipal) {
            return userPrincipal.getId();
        }
        throw new BusinessException("Invalid authenticated user", HttpStatus.UNAUTHORIZED);
    }

    private String generateAccountNumber() {
        String accountNumber;
        do {
            accountNumber = "100" + System.currentTimeMillis();
        } while (accountRepository.existsByAccountNumber(accountNumber));
        return accountNumber;
    }
}
