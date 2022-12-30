package com.codewithfibbee.ipay.service.flutterwave;

import com.codewithfibbee.ipay.config.WebClientHandler;
import com.codewithfibbee.ipay.enums.Provider;
import com.codewithfibbee.ipay.exceptions.InvalidRequestException;
import com.codewithfibbee.ipay.model.TransactionHistory;
import com.codewithfibbee.ipay.payloads.request.BankTransferDto;
import com.codewithfibbee.ipay.payloads.request.FlwTransferRequest;
import com.codewithfibbee.ipay.payloads.request.ValidateAccountDto;
import com.codewithfibbee.ipay.payloads.response.FlwTransferResponse;
import com.codewithfibbee.ipay.payloads.response.ListBanksResponse;
import com.codewithfibbee.ipay.payloads.response.TransferResponse;
import com.codewithfibbee.ipay.payloads.response.ValidateAccountResponse;
import com.codewithfibbee.ipay.repository.TransactionHistoryRepository;
import com.codewithfibbee.ipay.service.IPayService;
import com.codewithfibbee.ipay.util.BaseUtil;
import com.google.gson.Gson;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.http.HttpRequest;
import java.util.List;
import java.util.Optional;

import static com.codewithfibbee.ipay.constants.ApiConstants.*;
import static com.codewithfibbee.ipay.util.BaseUtil.convertToJsonBody;
import static com.codewithfibbee.ipay.util.BaseUtil.headers;

@Service("FlutterWaveProvider")
@Slf4j
@RequiredArgsConstructor
public class FlutterWaveProviderServiceImpl implements IPayService {
    private final WebClientHandler webClientHandler;
    private final TransactionHistoryRepository repository;
    private final Gson gson;
    @Value("${flw-secret-key}")
    private String FLW_AUTH;

    @Override
    public List<ListBanksResponse> fetchBanks() {

        HttpRequest request = HttpRequest.newBuilder()
                .headers(headers(FLW_AUTH))
                .uri(URI.create(String.format("%s%s", FLW_BASE_URI, FLW_LIST_BANKS_URI)))
                .build();

        return webClientHandler.getListBanksResponses(request);
    }

    @Override
    public ValidateAccountResponse validateBankAccount(ValidateAccountDto dto) {

        String jsonBody = convertToJsonBody(dto);

        HttpRequest request = HttpRequest.newBuilder()
                .headers(headers(FLW_AUTH))
                .uri(URI.create(String.format("%s%s", FLW_BASE_URI, FLW_VALIDATE_BANK_URI)))
                .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                .build();

        var bank = fetchBanks().stream().filter(b -> b.getCode().equals(dto.getBankCode()))
                .findFirst().get();

        return webClientHandler.processValidateAccountResponse(dto, request, bank.getName());
    }

    @Override
    public TransferResponse transferFunds(BankTransferDto dto) {

        FlwTransferRequest transferRequest = buildFlwTransferRequest(dto);

        String jsonBody = convertToJsonBody(transferRequest);

        HttpRequest request = HttpRequest.newBuilder()
                .headers(headers(FLW_AUTH))
                .uri(URI.create(String.format("%s%s", FLW_BASE_URI, FLW_TRANSFER_URI)))
                .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                .build();

        FlwTransferResponse response = webClientHandler.getBaseResponseCompletableFuture(request)
                .thenApply(responseBody -> {
                    if (responseBody.getData() != null) {
                        return gson.fromJson(gson.toJson(responseBody.getData()), FlwTransferResponse.class);
                    } else {
                        throw new InvalidRequestException(responseBody.getMessage());
                    }
                }).join();

        repository.save(mapToTransactionHistoryEntity(response));
        return mapToTransferResponse(response);
    }

    @Override
    public Optional<String> getTransactionStatus(String transactionReference) {
        HttpRequest request = HttpRequest.newBuilder()
                .headers(headers(FLW_AUTH))
                .uri(URI.create(String.format("%s%s", FLW_BASE_URI, FLW_VERIFY_TRANSACTION_STATUS_URI)
                        .concat("?tx_ref=")
                        .concat(transactionReference)))
                .build();

        return Optional.ofNullable(webClientHandler.processFieldValue(request, "status"));
    }

    private FlwTransferRequest buildFlwTransferRequest(BankTransferDto dto) {
        return FlwTransferRequest.builder()
                .amount(dto.getAmount())
                .accountNumber(dto.getBeneficiaryAccountNumber())
                .accountBank(dto.getBeneficiaryBankCode())
                .currency(dto.getCurrencyCode())
                .narration(dto.getNarration())
                .reference(BaseUtil.generateUniqueRef())
                .debitCurrency(dto.getDebitCurrency())
                .callbackUrl(dto.getCallBackUrl())
                .build();
    }

    private TransferResponse mapToTransferResponse(FlwTransferResponse response) {
        return TransferResponse.builder()
                .amount(response.getAmount().toString())
                .beneficiaryAccountNumber(response.getAccount_number())
                .beneficiaryAccountName(response.getFull_name())
                .beneficiaryBankCode(response.getBank_code())
                .transactionReference(response.getReference())
                .transactionDateTime(response.getCreated_at())
                .currencyCode(response.getCurrency())
                .responseMessage(response.getComplete_message())
                .status(response.getStatus())
                .build();
    }

    private TransactionHistory mapToTransactionHistoryEntity(FlwTransferResponse response) {
        return TransactionHistory.builder()
                .amount(response.getAmount())
                .beneficiaryAccountNumber(response.getAccount_number())
                .beneficiaryAccountName(response.getFull_name())
                .beneficiaryBankCode(response.getBank_code())
                .transactionReference(response.getReference())
                .transactionDateTime(response.getCreated_at())
                .currencyCode(response.getCurrency())
                .status(response.getStatus())
                .responseMessage(response.getStatus().equalsIgnoreCase("success")
                        ? "Transfer_Successful"
                        : response.getStatus().equalsIgnoreCase("failure")
                        ? "Transfer_Not_Successful"
                        : "Pending")
                .provider(Provider.FlutterWave.name())
                .build();
    }
}
