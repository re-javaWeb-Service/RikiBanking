package com.re.rikkeibanking.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class TransactionStatementDto {
    private Long transactionId;
    private String transactionCode;
    private String accountNumber;
    private String counterpartyAccountNumber;
    private BigDecimal amount;
    private String transactionDirection;
    private String description;
    private String status;
    private LocalDateTime createdAt;
}
