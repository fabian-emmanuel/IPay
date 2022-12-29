package com.codewithfibbee.ipay.service;

import com.codewithfibbee.ipay.payloads.response.TransferResponse;

public interface IPayTransactionStatusService {
    TransferResponse getTransactionStatus(String reference);
}
