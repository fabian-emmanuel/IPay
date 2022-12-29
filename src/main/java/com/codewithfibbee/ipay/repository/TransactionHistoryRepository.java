package com.codewithfibbee.ipay.repository;

import com.codewithfibbee.ipay.model.TransactionHistory;
import com.codewithfibbee.ipay.payloads.response.TransferResponse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TransactionHistoryRepository extends JpaRepository<TransactionHistory, Long> {

    @Query("SELECT new com.codewithfibbee.ipay.payloads.response.TransferResponse(t.amount, t.beneficiaryAccountNumber, t.beneficiaryAccountName, t.beneficiaryBankCode, t.transactionReference, t.transactionDateTime, t.currencyCode, t.responseMessage, t.responseCode, t.sessionID, t.status) " +
           "FROM TransactionHistory t " +
           "WHERE t.transactionReference = ?1")
    Optional<TransferResponse> findByTransactionReference(String reference);
}
