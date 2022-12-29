package com.codewithfibbee.ipay.payloads.request;

import com.codewithfibbee.ipay.util.BaseUtil;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Positive;
import lombok.Value;

import java.math.BigDecimal;

@Value
public class BankTransferDto {
    @Positive
    BigDecimal amount = BigDecimal.ZERO;
    @NotEmpty
    @JsonProperty("currency")
    String currencyCode = "NGN";
    @NotEmpty
    String narration;
    @NotEmpty
    @JsonProperty("account_number")
    String beneficiaryAccountNumber;
//    @NotEmpty
//    @JsonProperty("account_name")
//    String beneficiaryAccountName;
    @NotEmpty
    @JsonProperty("account_bank")
    String beneficiaryBankCode;
//    @NotEmpty
    @JsonProperty("reference")
    String transactionReference;
    int maxRetryAttempt = 0;
    @JsonProperty("callback_url")
    String callBackUrl;
    @JsonProperty("debit_currency")
    String debitCurrency = "NGN";

}
