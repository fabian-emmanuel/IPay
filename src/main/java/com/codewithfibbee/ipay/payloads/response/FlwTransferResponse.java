package com.codewithfibbee.ipay.payloads.response;

import lombok.Builder;
import lombok.Value;

import java.math.BigDecimal;

@Value
@Builder
public class FlwTransferResponse {
    BigDecimal amount;
    String account_number;
    String full_name;
    String bank_code;
    String reference;
    String created_at;
    String currency;
    String complete_message;
    String status;
}
