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
import com.codewithfibbee.ipay.service.IPayProviderService;
import com.codewithfibbee.ipay.util.BaseUtil;
import com.google.gson.Gson;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.http.HttpRequest;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

import static com.codewithfibbee.ipay.constants.ApiConstants.*;
import static com.codewithfibbee.ipay.util.BaseUtil.convertToJsonBody;
import static com.codewithfibbee.ipay.util.BaseUtil.headers;

@Service("FlutterWaveProvider")
@Slf4j
@RequiredArgsConstructor
public class FlutterWaveProviderProviderServiceImpl implements IPayProviderService {
    private final WebClientHandler webClientHandler;
    private final TransactionHistoryRepository repository;
    private final Gson gson;
    @Value("${flw-secret-key}")
    private String FLW_AUTH;
    private static final int MAX_RETRY_ATTEMPT = 3; // Maximum number of retry attempts
    private static final int INITIAL_DELAY = 100; // Initial delay in milliseconds
    private static final int MULTIPLIER = 2; // Back-off multiplier

    @Override
    public List<ListBanksResponse> fetchBanks() {

        HttpRequest request = HttpRequest.newBuilder()
                .headers(headers(FLW_AUTH))
                .uri(URI.create(String.format("%s%s", FLW_BASE_URI, FLW_LIST_BANKS_URI)))
                .build();

        return webClientHandler.getListBanksResponses(request);
    }

    @Override
    public ValidateAccountResponse validateBankAccount(ValidateAccountDto dto, String bankName) {

        String jsonBody = convertToJsonBody(dto);

        HttpRequest request = HttpRequest.newBuilder()
                .headers(headers(FLW_AUTH))
                .uri(URI.create(String.format("%s%s", FLW_BASE_URI, FLW_VALIDATE_BANK_URI)))
                .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                .build();

        return webClientHandler.processValidateAccountResponse(dto, request, bankName);
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
    public Optional<String> getTransactionStatusValue(String transactionReference) {
        HttpRequest request = HttpRequest.newBuilder()
                .headers(headers(FLW_AUTH))
                .uri(URI.create(String.format("%s%s", FLW_BASE_URI, FLW_VERIFY_TRANSACTION_STATUS_URI)
                        .concat("?tx_ref=")
                        .concat(transactionReference)))
                .build();

        return Optional.ofNullable(webClientHandler.processFieldValue(request, "status"));
    }


    @Override
    public void doRetry(String transactionReference) {
        int retryAttempt = 0; // Current retry attempt
        int delay = INITIAL_DELAY; // Current delay
        AtomicReference<String> m= new AtomicReference<>("");

        while (retryAttempt < MAX_RETRY_ATTEMPT) {
            try {
                HttpRequest request = HttpRequest.newBuilder()
                        .headers(headers(FLW_AUTH))
                        .uri(URI.create(String.format("%s%s", FLW_BASE_URI, FLW_TRANSFER_URI)
                                .concat(String.format("/%s/retries", transactionReference))))
                        .POST(HttpRequest.BodyPublishers.ofString(""))
                        .build();

                var status = Optional.ofNullable(webClientHandler.processFieldValue(request, "status"));
                status.ifPresent(s -> {
                    if (s.equals("success")) {
                        log.info("Transaction retry successful");
                    } else {
                        m.set(s);
                        log.info("Message : {} ", s);
                        log.info("Transaction retry failed");
                    }
                });
                break;

            } catch (Exception e) {
                retryAttempt++; // Increment the retry attempt
                log.info("attempt: {}", retryAttempt);
                if (retryAttempt == MAX_RETRY_ATTEMPT) {
                    throw new InvalidRequestException(m.get());
                }
                try {
                    // Exponential back-off
                    TimeUnit.MILLISECONDS.sleep(delay);
                    delay *= MULTIPLIER;
                } catch (InterruptedException ex) {
                    Thread.currentThread().interrupt();
                    // Handle the interrupt exception
                }
            }
        }

    }

    private FlwTransferRequest buildFlwTransferRequest(BankTransferDto dto) {
        return FlwTransferRequest.builder()
                .amount(dto.getAmount())
                .accountNumber(dto.getBeneficiaryAccountNumber())
                .accountBank(dto.getBeneficiaryBankCode())
                .currency(dto.getCurrencyCode())
                .narration(dto.getNarration())
                .reference(StringUtils.isBlank(dto.getTransactionReference()) ? BaseUtil.generateUniqueRef() : dto.getTransactionReference())
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
