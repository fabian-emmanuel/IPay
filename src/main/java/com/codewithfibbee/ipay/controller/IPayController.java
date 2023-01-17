package com.codewithfibbee.ipay.controller;

import com.codewithfibbee.ipay.config.cache.CacheManager;
import com.codewithfibbee.ipay.payloads.request.BankTransferDto;
import com.codewithfibbee.ipay.payloads.request.ValidateAccountDto;
import com.codewithfibbee.ipay.payloads.response.ListBanksResponse;
import com.codewithfibbee.ipay.payloads.response.TransferResponse;
import com.codewithfibbee.ipay.payloads.response.ValidateAccountResponse;
import com.codewithfibbee.ipay.service.IPayProviderService;
import com.codewithfibbee.ipay.service.IPayTransactionStatusService;
import jakarta.validation.Valid;
import lombok.Value;
import lombok.extern.slf4j.Slf4j;
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
    CacheManager cacheManager;

    @GetMapping("/banks")
    public ResponseEntity<List<ListBanksResponse>> fetchBanks(@RequestParam(required = false, defaultValue = "PayStack") String provider) {
        provider = validateProvider(provider);
        List<ListBanksResponse> banks = cacheManager.getProviderBanks(provider);
        List<ListBanksResponse> response = banks.isEmpty() ? banks : iPayServiceMap.get(provider).fetchBanks();
        return ResponseEntity.ok(response);
    }

    @PostMapping("/validate-bank-account")
    public ResponseEntity<ValidateAccountResponse> validateBankAccount(@Valid @RequestBody ValidateAccountDto validateAccountDto,
                                                                       @RequestParam(required = false, defaultValue = "PayStack") String provider) {
        provider = validateProvider(provider);
        String bankName = cacheManager.getBankName(validateAccountDto.getCode());
        return ResponseEntity.ok(iPayServiceMap.get(provider).validateBankAccount(validateAccountDto, bankName));
    }

    @PostMapping("/bank-transfer")
    public ResponseEntity<TransferResponse> bankTransfer(@Valid @RequestBody BankTransferDto bankTransferDto,
                                                         @RequestParam(required = false, defaultValue = "PayStack") String provider) {
        provider = validateProvider(provider);
        return ResponseEntity.ok(iPayServiceMap.get(provider).transferFunds(bankTransferDto));
    }

    @GetMapping("/transaction/{reference}")
    public ResponseEntity<TransferResponse> transactionStatus(@PathVariable String reference) {
        return ResponseEntity.ok(iPayTransactionStatusService.getTransactionStatus(reference));
    }

    @PostMapping("/retry/{reference}")
    public ResponseEntity<Object> retryTransaction(@PathVariable String reference) {
        iPayTransactionStatusService.doRetry(reference);
        return ResponseEntity.ok("");
    }
}
