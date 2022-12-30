package com.codewithfibbee.ipay.repository;

import com.codewithfibbee.ipay.model.TransactionHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TransactionHistoryRepository extends JpaRepository<TransactionHistory, Long> {

    @Query("SELECT t " +
           "FROM TransactionHistory t " +
           "WHERE t.transactionReference = ?1")
    Optional<TransactionHistory> findByTransactionReference(String reference);
}
