package com.re.rikkeibanking.service.impl;

import com.re.rikkeibanking.dto.request.TransferRequest;
import com.re.rikkeibanking.dto.response.TransferResponse;
import com.re.rikkeibanking.entity.Account;
import com.re.rikkeibanking.entity.BankingTransaction;
import com.re.rikkeibanking.entity.Role;
import com.re.rikkeibanking.entity.User;
import com.re.rikkeibanking.exception.BusinessException;
import com.re.rikkeibanking.repository.AccountRepository;
import com.re.rikkeibanking.repository.BankingTransactionRepository;
import com.re.rikkeibanking.security.UserPrincipal;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.math.BigDecimal;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TransferServiceImplTest {

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private BankingTransactionRepository transactionRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private Authentication authentication;

    private TransferServiceImpl transferService;
    private User customer;
    private Account sourceAccount;
    private Account targetAccount;

    @BeforeEach
    void setUp() {
        transferService = new TransferServiceImpl(accountRepository, transactionRepository, passwordEncoder);

        Role role = new Role();
        role.setName("ROLE_CUSTOMER");

        customer = new User();
        customer.setId(10L);
        customer.setUsername("customer");
        customer.setPassword("password");
        customer.setIsActive(true);
        customer.setIsKyc(true);
        customer.setRole(role);

        sourceAccount = account("1000001", new BigDecimal("1000.00"), customer);
        targetAccount = account("1000002", new BigDecimal("200.00"), customer);

        when(authentication.getPrincipal()).thenReturn(UserPrincipal.from(customer));
    }

    @Test
    void transferSuccessMovesBalanceAndCreatesTransaction() {
        TransferRequest request = transferRequest(new BigDecimal("150.00"));
        when(accountRepository.findByAccountNumberForUpdate("1000001")).thenReturn(Optional.of(sourceAccount));
        when(accountRepository.findByAccountNumberForUpdate("1000002")).thenReturn(Optional.of(targetAccount));
        when(passwordEncoder.matches("123456", "encoded-pin")).thenReturn(true);
        when(transactionRepository.save(any(BankingTransaction.class))).thenAnswer(invocation -> {
            BankingTransaction transaction = invocation.getArgument(0);
            transaction.setId(99L);
            return transaction;
        });

        TransferResponse response = transferService.transfer(request, authentication);

        assertThat(sourceAccount.getBalance()).isEqualByComparingTo("850.00");
        assertThat(targetAccount.getBalance()).isEqualByComparingTo("350.00");
        assertThat(response.getTransactionId()).isEqualTo(99L);
        assertThat(response.getStatus()).isEqualTo("SUCCESS");

        ArgumentCaptor<BankingTransaction> transactionCaptor = ArgumentCaptor.forClass(BankingTransaction.class);
        verify(transactionRepository).save(transactionCaptor.capture());
        assertThat(transactionCaptor.getValue().getTransactionCode()).startsWith("TXN-");
    }

    @Test
    void transferFailsWhenBalanceIsInsufficient() {
        TransferRequest request = transferRequest(new BigDecimal("1200.00"));
        when(accountRepository.findByAccountNumberForUpdate("1000001")).thenReturn(Optional.of(sourceAccount));
        when(accountRepository.findByAccountNumberForUpdate("1000002")).thenReturn(Optional.of(targetAccount));

        assertThatThrownBy(() -> transferService.transfer(request, authentication))
                .isInstanceOf(BusinessException.class)
                .hasMessage("Insufficient balance");

        assertThat(sourceAccount.getBalance()).isEqualByComparingTo("1000.00");
        assertThat(targetAccount.getBalance()).isEqualByComparingTo("200.00");
        verify(transactionRepository, never()).save(any(BankingTransaction.class));
    }

    private Account account(String accountNumber, BigDecimal balance, User user) {
        Account account = new Account();
        account.setAccountNumber(accountNumber);
        account.setBalance(balance);
        account.setCurrency("VND");
        account.setActive(true);
        account.setTransactionPin("encoded-pin");
        account.setUser(user);
        return account;
    }

    private TransferRequest transferRequest(BigDecimal amount) {
        TransferRequest request = new TransferRequest();
        request.setFromAccountNumber("1000001");
        request.setToAccountNumber("1000002");
        request.setAmount(amount);
        request.setDescription("test transfer");
        request.setTransactionPin("123456");
        return request;
    }
}
