package com.codewithfibbee.ipay.payloads.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Value;

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
}
