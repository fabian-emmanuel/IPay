package com.codewithfibbee.ipay.service;

import com.codewithfibbee.ipay.payloads.request.BankTransferDto;
import com.codewithfibbee.ipay.payloads.request.ValidateAccountDto;
import com.codewithfibbee.ipay.payloads.response.ListBanksResponse;
import com.codewithfibbee.ipay.payloads.response.TransferResponse;

import java.util.List;

public interface IPayService {
    List<ListBanksResponse> fetchBanks();
    Object validateBankAccount(ValidateAccountDto validateAccountDto);
    TransferResponse transferFunds(BankTransferDto bankTransferDto);
}
