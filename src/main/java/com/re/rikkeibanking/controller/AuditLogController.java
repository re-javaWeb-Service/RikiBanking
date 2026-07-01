package com.re.rikkeibanking.controller;

import com.re.rikkeibanking.dto.response.ApiResponse;
import com.re.rikkeibanking.dto.response.AuditLogResponseDto;
import com.re.rikkeibanking.service.AuditLogService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/audit-logs")
public class AuditLogController {

    private final AuditLogService auditLogService;

    @GetMapping
    public ResponseEntity<ApiResponse<Page<AuditLogResponseDto>>> getAuditLogs(Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.ok(auditLogService.getAuditLogs(pageable)));
    }
}
