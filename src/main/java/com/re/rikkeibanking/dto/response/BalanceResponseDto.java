package com.re.rikkeibanking.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@AllArgsConstructor
@Getter
@Setter
public class BalanceResponseDto {
    private String accountNumber;
    private String currency;
    private BigDecimal balance;
}
