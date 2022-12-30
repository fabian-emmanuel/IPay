package com.codewithfibbee.ipay.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.Table;

import java.math.BigDecimal;


@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Table(appliesTo = "transaction_history")
public class TransactionHistory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;
    BigDecimal amount;
    String beneficiaryAccountNumber;
    String beneficiaryAccountName;
    String beneficiaryBankCode;
    String transactionReference;
    String transactionDateTime;
    String currencyCode;
    String responseMessage;
    String responseCode;
    String sessionID;
    String status;
    String provider;

    @Override
    public String toString() {
        return "TransactionHistory{id=%d, amount=%s, beneficiaryAccountNumber='%s', beneficiaryAccountName='%s', beneficiaryBankCode='%s', transactionReference='%s', transactionDateTime='%s', currencyCode='%s', responseMessage='%s', responseCode='%s', sessionID='%s', status='%s', provider='%s'}".formatted(id, amount, beneficiaryAccountNumber, beneficiaryAccountName, beneficiaryBankCode, transactionReference, transactionDateTime, currencyCode, responseMessage, responseCode, sessionID, status, provider);
    }
}
