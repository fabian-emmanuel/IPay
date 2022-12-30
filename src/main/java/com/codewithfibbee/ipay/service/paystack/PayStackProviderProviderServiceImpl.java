package com.codewithfibbee.ipay.service.paystack;

import com.codewithfibbee.ipay.config.WebClientHandler;
import com.codewithfibbee.ipay.enums.Provider;
import com.codewithfibbee.ipay.exceptions.InvalidRequestException;
import com.codewithfibbee.ipay.model.TransactionHistory;
import com.codewithfibbee.ipay.payloads.request.BankTransferDto;
import com.codewithfibbee.ipay.payloads.request.PaystackTransferRecipientRequest;
import com.codewithfibbee.ipay.payloads.request.PaystackTransferRequest;
import com.codewithfibbee.ipay.payloads.request.ValidateAccountDto;
import com.codewithfibbee.ipay.payloads.response.*;
import com.codewithfibbee.ipay.repository.TransactionHistoryRepository;
import com.codewithfibbee.ipay.service.IPayProviderService;
import com.codewithfibbee.ipay.util.BaseUtil;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.http.HttpRequest;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static com.codewithfibbee.ipay.constants.ApiConstants.*;
import static com.codewithfibbee.ipay.util.BaseUtil.convertToJsonBody;
import static com.codewithfibbee.ipay.util.BaseUtil.headers;

@Service("PayStackProvider")
@Slf4j
@RequiredArgsConstructor
public class PayStackProviderProviderServiceImpl implements IPayProviderService {
    private final Gson gson;
    private final WebClientHandler webClientHandler;
    private final TransactionHistoryRepository repository;
    @Value("${pstk-secret-key}")
    private String PSTK_AUTH;

    @Override
    public List<ListBanksResponse> fetchBanks() {

        HttpRequest request = HttpRequest.newBuilder()
                .headers(headers(PSTK_AUTH))
                .uri(URI.create(String.format("%s%s", PSTK_BASE_URI, PSTK_LIST_BANKS_URI)))
                .build();

        return webClientHandler.getListBanksResponses(request);
    }


    @Override
    public ValidateAccountResponse validateBankAccount(ValidateAccountDto validateAccountDto) {

        HttpRequest request = HttpRequest.newBuilder()
                .headers(headers(PSTK_AUTH))
                .uri(URI.create(String.format("%s%s", PSTK_BASE_URI, PSTK_VALIDATE_BANK_URI)
                        .concat("?account_number=")
                        .concat(validateAccountDto.getAccountNumber())
                        .concat("&bank_code=")
                        .concat(validateAccountDto.getBankCode())))
                .build();

        var bank = fetchBanks().stream().filter(b -> b.getCode().equals(validateAccountDto.getBankCode()))
                .findFirst().get();

        return webClientHandler.processValidateAccountResponse(validateAccountDto, request, bank.getName());
    }
    @Override
    public TransferResponse transferFunds(BankTransferDto bankTransferDto) {

        var recipient = createRecipient(
                bankTransferDto.getBeneficiaryAccountNumber(),
                bankTransferDto.getBeneficiaryBankCode()
        );

        String recipientCode = getRecipientCode(recipient);

        PaystackTransferRequest transferRequest = PaystackTransferRequest.builder()
                .amount(bankTransferDto.getAmount())
                .recipient(recipientCode)
                .reason(bankTransferDto.getNarration())
                .reference(BaseUtil.generateUniqueRef())
                .build();

        String jsonBody = convertToJsonBody(transferRequest);

        HttpRequest request = HttpRequest.newBuilder()
                .headers(headers(PSTK_AUTH))
                .uri(URI.create(String.format("%s%s", PSTK_BASE_URI, PSTK_TRANSFER_URI)))
                .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                .build();

        PayStackTransferResponse response = webClientHandler.getBaseResponseCompletableFuture(request)
                .thenApply(responseBody -> {
                    if (responseBody.getData() != null) {
                        return gson.fromJson(gson.toJson(responseBody.getData()), PayStackTransferResponse.class);
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
                .headers(headers(PSTK_AUTH))
                .uri(URI.create(String.format("%s%s", PSTK_BASE_URI, PSTK_VERIFY_TRANSACTION_STATUS_URI)
                        .concat(transactionReference)))
                .build();

        return Optional.ofNullable(webClientHandler.processFieldValue(request, "status"));
    }

    @Override
    public void doRetry(String transactionReference) {
        //Todo: implement retry logic
    }

    private TransferResponse mapToTransferResponse(PayStackTransferResponse response) {
        return TransferResponse.builder()
                .amount(response.getAmount().toString())
                .beneficiaryAccountNumber(response.getAccount_number())
                .beneficiaryAccountName(response.getName())
                .beneficiaryBankCode(response.getBank_code())
                .transactionReference(response.getReference())
                .transactionDateTime(response.getTransfer_date())
                .currencyCode(response.getCurrency())
                .responseMessage(response.getMessage())
                .status(response.getStatus())
                .build();
    }

    private String getRecipientCode(Object recipient) {
        return gson.fromJson(gson.toJson(recipient), JsonObject.class)
                .get("recipient_code").getAsString();
    }

    private Object createRecipient(String recipientAccountNumber, String recipientBankCode) {

        PaystackTransferRecipientRequest recipientRequest = PaystackTransferRecipientRequest.builder()
                .accountNumber(recipientAccountNumber)
                .bankCode(recipientBankCode)
                .type("nuban")
                .build();

        String jsonBody = convertToJsonBody(recipientRequest);
        HttpRequest request = HttpRequest.newBuilder()
                .headers(headers(PSTK_AUTH))
                .uri(URI.create(String.format("%s%s", PSTK_BASE_URI, PSTK_TRANSFER_RECIPIENT_URI)))
                .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                .build();

        var response = webClientHandler.processResponse(request);

        if (Objects.nonNull(response)) {
            if (Objects.equals(response.getStatus(), "true")) {
                return response.getData();
            } else {
                throw new InvalidRequestException(response.getMessage());
            }
        }
        return null;
    }

    private TransactionHistory mapToTransactionHistoryEntity(PayStackTransferResponse response) {
        return TransactionHistory.builder()
                .amount(response.getAmount())
                .beneficiaryAccountNumber(response.getAccount_number())
                .beneficiaryAccountName(response.getName())
                .beneficiaryBankCode(response.getBank_code())
                .transactionReference(response.getReference())
                .transactionDateTime(response.getTransfer_date())
                .currencyCode(response.getCurrency())
                .status(response.getStatus())
                .responseMessage(response.getStatus().equalsIgnoreCase("success")
                        ? "Transfer_Successful"
                        : response.getStatus().equalsIgnoreCase("failure")
                        ? "Transfer_Not_Successful"
                        : "Pending")
                .provider(Provider.PayStack.name())
                .build();
    }
}
