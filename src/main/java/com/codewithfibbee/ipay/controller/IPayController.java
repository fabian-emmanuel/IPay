package com.codewithfibbee.ipay.controller;

import com.codewithfibbee.ipay.apiresponse.ApiDataResponse;
import com.codewithfibbee.ipay.payloads.request.BankTransferDto;
import com.codewithfibbee.ipay.payloads.request.ValidateAccountDto;
import com.codewithfibbee.ipay.payloads.response.ListBanksResponse;
import com.codewithfibbee.ipay.payloads.response.TransferResponse;
import com.codewithfibbee.ipay.service.IPayService;
import com.codewithfibbee.ipay.service.IPayTransactionStatusService;
import com.codewithfibbee.ipay.util.ApiResponseUtil;
import jakarta.validation.Valid;
import lombok.Value;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import static com.codewithfibbee.ipay.util.BaseUtil.validateProvider;


@RestController
@RequestMapping("${api.basepath}")
@Value
@Slf4j
public class IPayController {
    IPayTransactionStatusService iPayTransactionStatusService;
    Map<String, IPayService> iPayServiceMap;

    @GetMapping("/banks")
    public ResponseEntity<ApiDataResponse<List<ListBanksResponse>>> fetchBanks(@RequestParam(required = false, defaultValue = "FlutterWave") String provider){
        provider = validateProvider(provider);
        return ApiResponseUtil.response(HttpStatus.OK, iPayServiceMap.get(provider).fetchBanks(), "Banks Retrieved Successfully");
    }

    @PostMapping("/validate-bank-account")
    public ResponseEntity<ApiDataResponse<Object>> validateBankAccount(@Valid @RequestBody ValidateAccountDto validateAccountDto, @RequestParam(required = false, defaultValue = "FlutterWave") String provider) {
        provider = validateProvider(provider);
        return ApiResponseUtil.response(HttpStatus.OK, iPayServiceMap.get(provider).validateBankAccount(validateAccountDto), "Account Validated Successfully");
    }

    @PostMapping("/bank-transfer")
    public ResponseEntity<ApiDataResponse<Object>> bankTransfer(@Valid @RequestBody BankTransferDto bankTransferDto, @RequestParam(required = false, defaultValue = "FlutterWave") String provider) {
        provider = validateProvider(provider);
        return ApiResponseUtil.response(HttpStatus.OK, iPayServiceMap.get(provider).transferFunds(bankTransferDto), "Funds Transfer Queued Successfully");
    }

    @GetMapping("/transaction/{reference}")
    public ResponseEntity<ApiDataResponse<TransferResponse>> transactionStatus(@PathVariable String reference) {
        return ApiResponseUtil.response(HttpStatus.OK, iPayTransactionStatusService.getTransactionStatus(reference), "Transaction Status Retrieved Successfully");
    }
}
