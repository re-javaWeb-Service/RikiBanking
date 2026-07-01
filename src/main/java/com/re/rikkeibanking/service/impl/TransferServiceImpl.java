package com.re.rikkeibanking.service.impl;

import com.re.rikkeibanking.aspect.LogAudit;
import com.re.rikkeibanking.dto.request.TransferRequest;
import com.re.rikkeibanking.dto.response.TransferResponse;
import com.re.rikkeibanking.entity.Account;
import com.re.rikkeibanking.entity.BankingTransaction;
import com.re.rikkeibanking.exception.BusinessException;
import com.re.rikkeibanking.repository.AccountRepository;
import com.re.rikkeibanking.repository.BankingTransactionRepository;
import com.re.rikkeibanking.security.UserPrincipal;
import com.re.rikkeibanking.service.TransferService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TransferServiceImpl implements TransferService {

    private final AccountRepository accountRepository;
    private final BankingTransactionRepository transactionRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    @LogAudit("TRANSFER")
    public TransferResponse transfer(TransferRequest request, Authentication authentication) {
        if (request.getFromAccountNumber().equals(request.getToAccountNumber())) {
            throw new BusinessException("Cannot transfer to the same account", HttpStatus.BAD_REQUEST);
        }

        Account firstLockedAccount;
        Account secondLockedAccount;
        if (request.getFromAccountNumber().compareTo(request.getToAccountNumber()) <= 0) {
            firstLockedAccount = findLockedAccount(request.getFromAccountNumber());
            secondLockedAccount = findLockedAccount(request.getToAccountNumber());
        } else {
            firstLockedAccount = findLockedAccount(request.getToAccountNumber());
            secondLockedAccount = findLockedAccount(request.getFromAccountNumber());
        }

        Account fromAccount = firstLockedAccount.getAccountNumber().equals(request.getFromAccountNumber())
                ? firstLockedAccount
                : secondLockedAccount;
        Account toAccount = firstLockedAccount.getAccountNumber().equals(request.getToAccountNumber())
                ? firstLockedAccount
                : secondLockedAccount;

        validateTransfer(request, fromAccount, toAccount, authentication);

        fromAccount.setBalance(fromAccount.getBalance().subtract(request.getAmount()));
        toAccount.setBalance(toAccount.getBalance().add(request.getAmount()));

        BankingTransaction transaction = new BankingTransaction();
        transaction.setTransactionCode(generateTransactionCode());
        transaction.setFromAccount(fromAccount);
        transaction.setToAccount(toAccount);
        transaction.setAmount(request.getAmount());
        transaction.setDescription(request.getDescription());
        transaction.setStatus("SUCCESS");

        BankingTransaction savedTransaction = transactionRepository.save(transaction);

        return new TransferResponse(
                savedTransaction.getId(),
                savedTransaction.getTransactionCode(),
                fromAccount.getAccountNumber(),
                toAccount.getAccountNumber(),
                savedTransaction.getAmount(),
                savedTransaction.getStatus(),
                savedTransaction.getCreatedAt()
        );
    }

    private Account findLockedAccount(String accountNumber) {
        return accountRepository.findByAccountNumberForUpdate(accountNumber)
                .orElseThrow(() -> new BusinessException("Account not found: " + accountNumber, HttpStatus.NOT_FOUND));
    }

    private void validateTransfer(TransferRequest request, Account fromAccount, Account toAccount, Authentication authentication) {
        Long currentUserId = currentUserId(authentication);
        if (!fromAccount.getUser().getId().equals(currentUserId)) {
            throw new BusinessException("You do not have permission to transfer from this account", HttpStatus.FORBIDDEN);
        }
        if (!Boolean.TRUE.equals(fromAccount.getUser().getIsKyc())) {
            throw new BusinessException("User must complete KYC before transfer", HttpStatus.FORBIDDEN);
        }
        validateActive(fromAccount);
        validateActive(toAccount);
        if (request.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new BusinessException("Transfer amount must be greater than zero", HttpStatus.BAD_REQUEST);
        }
        if (fromAccount.getBalance().compareTo(request.getAmount()) < 0) {
            throw new BusinessException("Insufficient balance", HttpStatus.CONFLICT);
        }
        if (!passwordEncoder.matches(request.getTransactionPin(), fromAccount.getTransactionPin())) {
            throw new BusinessException("Invalid transaction PIN", HttpStatus.FORBIDDEN);
        }
    }

    private void validateActive(Account account) {
        if (!Boolean.TRUE.equals(account.getActive())) {
            throw new BusinessException("Account is not active: " + account.getAccountNumber(), HttpStatus.CONFLICT);
        }
    }

    private Long currentUserId(Authentication authentication) {
        Object principal = authentication.getPrincipal();
        if (principal instanceof UserPrincipal userPrincipal) {
            return userPrincipal.getId();
        }
        throw new BusinessException("Invalid authenticated user", HttpStatus.UNAUTHORIZED);
    }

    private String generateTransactionCode() {
        return "TXN-" + UUID.randomUUID().toString().replace("-", "").substring(0, 16).toUpperCase();
    }
}
