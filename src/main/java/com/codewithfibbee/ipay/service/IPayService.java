package com.codewithfibbee.ipay.service;

import com.codewithfibbee.ipay.payloads.request.BankTransferDto;
import com.codewithfibbee.ipay.payloads.request.ValidateAccountDto;
import com.codewithfibbee.ipay.payloads.response.ListBanksResponse;
import com.codewithfibbee.ipay.payloads.response.TransferResponse;
import com.codewithfibbee.ipay.payloads.response.ValidateAccountResponse;

import java.util.List;
import java.util.Optional;

public interface IPayService {
    List<ListBanksResponse> fetchBanks();
    ValidateAccountResponse validateBankAccount(ValidateAccountDto validateAccountDto);
    TransferResponse transferFunds(BankTransferDto bankTransferDto);
    Optional<String> getTransactionStatus(String transactionReference);
}
