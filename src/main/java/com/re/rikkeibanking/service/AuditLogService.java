package com.re.rikkeibanking.service;

import com.re.rikkeibanking.dto.response.AuditLogResponseDto;
import com.re.rikkeibanking.entity.AuditLog;
import com.re.rikkeibanking.repository.AuditRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuditLogService {

    private final AuditRepository auditRepository;

    public Page<AuditLogResponseDto> getAuditLogs(Pageable pageable) {
        return auditRepository.findAll(pageable).map(this::toResponse);
    }

    private AuditLogResponseDto toResponse(AuditLog auditLog) {
        return new AuditLogResponseDto(
                auditLog.getId(),
                auditLog.getAction(),
                auditLog.getActor(),
                auditLog.getStatus(),
                auditLog.getMessage(),
                auditLog.getCreatedAt()
        );
    }
}
