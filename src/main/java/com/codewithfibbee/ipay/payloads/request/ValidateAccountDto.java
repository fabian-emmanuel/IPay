package com.codewithfibbee.ipay.payloads.request;

import jakarta.validation.constraints.NotEmpty;
import lombok.Value;

@Value
public class ValidateAccountDto {
    @NotEmpty
    String code;
    @NotEmpty
    String accountNumber;
}
