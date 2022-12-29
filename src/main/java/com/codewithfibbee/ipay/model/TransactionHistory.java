package com.codewithfibbee.ipay.model;

import com.codewithfibbee.ipay.enums.Provider;
import com.codewithfibbee.ipay.enums.Status;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.Table;

import java.math.BigDecimal;


@Entity
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
}
