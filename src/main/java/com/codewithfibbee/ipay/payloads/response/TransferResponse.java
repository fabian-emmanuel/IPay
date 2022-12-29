package com.codewithfibbee.ipay.payloads.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Value;

import java.math.BigDecimal;

@Value
@Builder
@AllArgsConstructor
public class TransferResponse {
    String amount;
    String beneficiaryAccountNumber;
    String beneficiaryAccountName;
    String beneficiaryBankCode;
    String transactionReference;
    String transactionDateTime;
    String currencyCode;
    String responseMessage;
    String responseCode;
    String sessionId;
    String status;

    public TransferResponse(BigDecimal amount,
                            String beneficiaryAccountNumber,
                            String beneficiaryAccountName,
                            String beneficiaryBankCode,
                            String transactionReference,
                            String transactionDateTime,
                            String currencyCode,
                            String responseMessage,
                            String responseCode,
                            String sessionId,
                            String status) {
        this.amount = amount.toString();
        this.beneficiaryAccountNumber = beneficiaryAccountNumber;
        this.beneficiaryAccountName = beneficiaryAccountName;
        this.beneficiaryBankCode = beneficiaryBankCode;
        this.transactionReference = transactionReference;
        this.transactionDateTime = transactionDateTime;
        this.currencyCode = currencyCode;
        this.responseMessage = responseMessage;
        this.responseCode = responseCode;
        this.sessionId = sessionId;
        this.status = status;
    }
}
