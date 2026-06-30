package com.re.rikkeibanking.repository;

import com.re.rikkeibanking.entity.BankingTransaction;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BankingTransactionRepository extends JpaRepository<BankingTransaction,Long> {

}
