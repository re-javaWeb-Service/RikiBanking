package com.re.rikkeibanking.repository;

import com.re.rikkeibanking.entity.AuditLog;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AuditRepository extends JpaRepository<AuditLog,Long> {
}
