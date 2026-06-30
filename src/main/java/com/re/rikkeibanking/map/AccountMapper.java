package com.re.rikkeibanking.map;

import com.re.rikkeibanking.dto.response.AccountResponseDto;
import com.re.rikkeibanking.dto.response.BalanceResponseDto;
import com.re.rikkeibanking.entity.Account;

public class AccountMapper {
    private AccountMapper() {
    }

    public static AccountResponseDto toResponseDto(Account account) {
        return new AccountResponseDto(
                account.getId(),
                account.getAccountNumber(),
                account.getCurrency(),
                account.getBalance(),
                account.getActive(),
                account.getUser().getId(),
                account.getCreatedAt()
        );
    }

    public static BalanceResponseDto toBalanceResponseDto(Account account) {
        return new BalanceResponseDto(
                account.getAccountNumber(),
                account.getCurrency(),
                account.getBalance()
        );
    }
}
