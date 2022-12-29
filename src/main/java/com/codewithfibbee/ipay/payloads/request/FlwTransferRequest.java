package com.codewithfibbee.ipay.payloads.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.math.BigDecimal;

@Value
@Builder
public class FlwTransferRequest {
    @JsonProperty("account_bank")
    String  accountBank;
    @JsonProperty("account_number")
    String accountNumber;
    BigDecimal amount;
    String narration;
    String currency;
    String reference;
    @JsonProperty("callback_url")
    String callbackUrl;
    @JsonProperty("debit_currency")
    String  debitCurrency;

}
