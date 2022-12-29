package com.codewithfibbee.ipay.service;

import com.codewithfibbee.ipay.config.WebClientHandler;
import com.codewithfibbee.ipay.exceptions.InvalidRequestException;
import com.codewithfibbee.ipay.payloads.request.BankTransferDto;
import com.codewithfibbee.ipay.payloads.request.PaystackTransferRecipientRequest;
import com.codewithfibbee.ipay.payloads.request.PaystackTransferRequest;
import com.codewithfibbee.ipay.payloads.request.ValidateAccountDto;
import com.codewithfibbee.ipay.payloads.response.ListBanksResponse;
import com.codewithfibbee.ipay.payloads.response.TransferResponse;
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

import static com.codewithfibbee.ipay.constants.ApiConstants.*;
import static com.codewithfibbee.ipay.util.BaseUtil.convertToJsonBody;
import static com.codewithfibbee.ipay.util.BaseUtil.headers;

@Service("PayStackProvider")
@Slf4j
@RequiredArgsConstructor
public class PayStackProviderServiceImpl implements IPayService {
    private final Gson gson;
    private final WebClientHandler webClientHandler;
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
    public Object validateBankAccount(ValidateAccountDto validateAccountDto) {

        HttpRequest request = HttpRequest.newBuilder()
                .headers(headers(PSTK_AUTH))
                .uri(URI.create(String.format("%s%s", PSTK_BASE_URI, PSTK_VALIDATE_BANK_URI)
                        .concat("?account_number=")
                        .concat(validateAccountDto.getAccountNumber())
                        .concat("&bank_code=")
                        .concat(validateAccountDto.getBankCode())))
                .build();

        return webClientHandler.processResponse(request);
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

//        return webClientHandler.processResponse(request);
        return null;
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
}
