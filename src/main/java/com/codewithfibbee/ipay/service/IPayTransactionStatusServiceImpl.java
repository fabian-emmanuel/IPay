package com.codewithfibbee.ipay.service;

import com.codewithfibbee.ipay.exceptions.ResourceNotFoundException;
import com.codewithfibbee.ipay.model.TransactionHistory;
import com.codewithfibbee.ipay.payloads.response.TransferResponse;
import com.codewithfibbee.ipay.repository.TransactionHistoryRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Map;

import static com.codewithfibbee.ipay.util.BaseUtil.validateProvider;

@Service
@Slf4j
@RequiredArgsConstructor
public class IPayTransactionStatusServiceImpl implements IPayTransactionStatusService {
    private final TransactionHistoryRepository repository;
    private final Map<String, IPayService> iPayServiceMap;

    @Override
    @Transactional
    public TransferResponse getTransactionStatus(String reference) {
         var res =  repository.findByTransactionReference(reference)
                 .orElseThrow(() -> new ResourceNotFoundException("Transaction reference not found"));
         log.info("Transaction status: {}", res);
         String provider = validateProvider(res.getProvider());
         var newStatus = iPayServiceMap.get(provider).getTransactionStatus(res.getTransactionReference());
         newStatus.ifPresent(res::setStatus);
         return mapToTransferResponse(res);
    }

    private TransferResponse mapToTransferResponse(TransactionHistory history) {
        return TransferResponse.builder()
                .amount(history.getAmount().toString())
                .beneficiaryAccountNumber(history.getBeneficiaryAccountNumber())
                .beneficiaryAccountName(history.getBeneficiaryAccountName())
                .beneficiaryBankCode(history.getBeneficiaryBankCode())
                .transactionReference(history.getTransactionReference())
                .transactionDateTime(history.getTransactionDateTime())
                .currencyCode(history.getCurrencyCode())
                .responseMessage(history.getResponseMessage())
                .status(history.getStatus())
                .build();
    }
}
