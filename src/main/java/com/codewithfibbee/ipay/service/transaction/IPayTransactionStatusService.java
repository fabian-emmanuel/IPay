package com.codewithfibbee.ipay.service.transaction;

import com.codewithfibbee.ipay.payloads.response.TransferResponse;

public interface IPayTransactionStatusService {
    TransferResponse getTransactionStatus(String reference);
    void doRetry(String reference);
}
