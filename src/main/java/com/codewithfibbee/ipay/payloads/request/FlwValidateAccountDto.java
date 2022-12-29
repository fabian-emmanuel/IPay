package com.codewithfibbee.ipay.payloads.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class FlwValidateAccountDto {
    @JsonProperty("account_number")
    String accountNumber;
    @JsonProperty("account_bank")
    String accountBank;
    String country = "ng";
}
