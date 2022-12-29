package com.codewithfibbee.ipay.service;

import com.codewithfibbee.ipay.exceptions.ResourceNotFoundException;
import com.codewithfibbee.ipay.payloads.response.TransferResponse;
import com.codewithfibbee.ipay.repository.TransactionHistoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class IPayTransactionStatusServiceImpl implements IPayTransactionStatusService {
    private final TransactionHistoryRepository repository;

    @Override
    public TransferResponse getTransactionStatus(String reference) {
         return repository.findByTransactionReference(reference)
                 .orElseThrow(() -> new ResourceNotFoundException("Transaction reference not found"));
    }
}
