package com.re.rikkeibanking.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class AuditLogResponseDto {
    private Long id;
    private String action;
    private String actor;
    private String status;
    private String message;
    private LocalDateTime createdAt;
}
