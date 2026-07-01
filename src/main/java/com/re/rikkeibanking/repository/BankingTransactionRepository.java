package com.re.rikkeibanking.repository;

import com.re.rikkeibanking.entity.BankingTransaction;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface BankingTransactionRepository extends JpaRepository<BankingTransaction,Long> {
    @EntityGraph(attributePaths = {"fromAccount", "toAccount"})
    @Query("""
            select t from BankingTransaction t
            where t.fromAccount.id = :accountId or t.toAccount.id = :accountId
            """)
    Page<BankingTransaction> findStatementsByAccountId(Long accountId, Pageable pageable);

}
