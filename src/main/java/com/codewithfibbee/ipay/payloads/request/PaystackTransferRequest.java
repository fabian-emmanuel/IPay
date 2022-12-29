package com.codewithfibbee.ipay.payloads.request;

import lombok.Builder;
import lombok.Value;

import java.math.BigDecimal;

@Value
@Builder
public class PaystackTransferRequest {
    String source;
    String reason;
    BigDecimal amount;
    String recipient;
    String reference;
}
