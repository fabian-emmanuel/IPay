package com.codewithfibbee.ipay.payloads.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotEmpty;
import lombok.Value;

@Value
public class ValidateAccountDto {
    @NotEmpty
    @JsonProperty("account_bank")
    String bankCode;
    @NotEmpty
    @JsonProperty("account_number")
    String accountNumber;
}
