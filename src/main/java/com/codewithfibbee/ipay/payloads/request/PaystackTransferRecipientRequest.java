package com.codewithfibbee.ipay.payloads.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class PaystackTransferRecipientRequest {
    String type;
    String name;
    @JsonProperty("account_number")
    String accountNumber;
    @JsonProperty("bank_code")
    String bankCode;
    String currency="NGN";
}
