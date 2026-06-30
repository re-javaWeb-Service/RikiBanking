package com.re.rikkeibanking.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;


@Getter
@Setter
@AllArgsConstructor
public class AccountResponseDto {
    private Long id;
    private String accountNumber;
    private String currency;
    private BigDecimal balance;
    private Boolean active;
    private Long userId;
    private LocalDateTime createdAt;
}

