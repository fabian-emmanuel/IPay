package com.codewithfibbee.ipay.controller;

import com.codewithfibbee.ipay.apiresponse.ApiResponse;
import com.codewithfibbee.ipay.payloads.request.BankTransferDto;
import com.codewithfibbee.ipay.payloads.request.ValidateAccountDto;
import com.codewithfibbee.ipay.payloads.response.ListBanksResponse;
import com.codewithfibbee.ipay.payloads.response.TransferResponse;
import com.codewithfibbee.ipay.payloads.response.ValidateAccountResponse;
import com.codewithfibbee.ipay.service.IPayProviderService;
import com.codewithfibbee.ipay.service.IPayTransactionStatusService;
import com.codewithfibbee.ipay.util.ApiResponseUtil;
import jakarta.validation.Valid;
import lombok.Value;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

import static com.codewithfibbee.ipay.util.BaseUtil.validateProvider;


@RestController
@RequestMapping("${api.basepath}")
@Value
@Slf4j
public class IPayController {
    IPayTransactionStatusService iPayTransactionStatusService;
    Map<String, IPayProviderService> iPayServiceMap;

    @GetMapping("/banks")
    public ResponseEntity<ApiResponse<List<ListBanksResponse>>> fetchBanks(@RequestParam(required = false, defaultValue = "PayStack") String provider){
        provider = validateProvider(provider);
        return ApiResponseUtil.response(HttpStatus.OK, iPayServiceMap.get(provider).fetchBanks(), "Banks Retrieved Successfully");
    }

    @PostMapping("/validate-bank-account")
    public ResponseEntity<ApiResponse<ValidateAccountResponse>> validateBankAccount(@Valid @RequestBody ValidateAccountDto validateAccountDto, @RequestParam(required = false, defaultValue = "PayStack") String provider) {
        provider = validateProvider(provider);
        return ApiResponseUtil.response(HttpStatus.OK, iPayServiceMap.get(provider).validateBankAccount(validateAccountDto), "Account Validated Successfully");
    }

    @PostMapping("/bank-transfer")
    public ResponseEntity<ApiResponse<TransferResponse>> bankTransfer(@Valid @RequestBody BankTransferDto bankTransferDto, @RequestParam(required = false, defaultValue = "PayStack") String provider) {
        provider = validateProvider(provider);
        return ApiResponseUtil.response(HttpStatus.OK, iPayServiceMap.get(provider).transferFunds(bankTransferDto), "Funds Transfer Queued Successfully");
    }

    @GetMapping("/transaction/{reference}")
    public ResponseEntity<ApiResponse<TransferResponse>> transactionStatus(@PathVariable String reference) {
        return ApiResponseUtil.response(HttpStatus.OK, iPayTransactionStatusService.getTransactionStatus(reference), "Transaction Status Retrieved Successfully");
    }

    @PostMapping("/retry/{reference}")
    public ResponseEntity<ApiResponse<Object>> retryTransaction(@PathVariable String reference) {
        iPayTransactionStatusService.doRetry(reference);
        return ApiResponseUtil.response(HttpStatus.OK, "", "Retry Attempted");
    }
}
