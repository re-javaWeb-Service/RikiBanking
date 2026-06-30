package com.re.rikkeibanking.repository;

import com.re.rikkeibanking.entity.Account;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AccountRepository extends JpaRepository<Account,Long> {

    //findByUserId: customer xem danh sách tài khoản của mình.
    Page<Account> findByUserId(Long userId, Pageable pageable);

    //findByIdAndUserId: kiểm tra tài khoản có thuộc user hiện tại không.
    Optional<Account> findByIdAndUserId(Long id, Long userId);

    //existsByAccountNumber: tránh sinh trùng số tài khoản.
    boolean existsByAccountNumber(String accountNumber);

}
