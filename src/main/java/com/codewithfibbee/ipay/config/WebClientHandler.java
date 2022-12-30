package com.codewithfibbee.ipay.config;

import com.codewithfibbee.ipay.exceptions.InvalidRequestException;
import com.codewithfibbee.ipay.exceptions.ProcessingException;
import com.codewithfibbee.ipay.payloads.request.ValidateAccountDto;
import com.codewithfibbee.ipay.payloads.response.BaseResponse;
import com.codewithfibbee.ipay.payloads.response.ListBanksResponse;
import com.codewithfibbee.ipay.payloads.response.ValidateAccountResponse;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import static com.codewithfibbee.ipay.util.BaseUtil.getMapper;


@RequiredArgsConstructor
@Slf4j
public class WebClientHandler {
    private final HttpClient client;
    private final Gson gson;

    public List<ListBanksResponse> getListBanksResponses(HttpRequest request) {
        try {
            var response = getBaseResponseCompletableFuture(request)
                    .thenApply(BaseResponse::getData)
                    .get();

            return gson.fromJson(gson.toJson(response), new TypeToken<List<ListBanksResponse>>() {
            }.getType());
        } catch (InterruptedException | ExecutionException e) {
            Thread.currentThread().interrupt();
        }
        return Collections.emptyList();
    }

    public BaseResponse processResponse(HttpRequest request) {
        try {
            return getBaseResponseCompletableFuture(request)
                    .get();
        } catch (InterruptedException | ExecutionException e) {
            Thread.currentThread().interrupt();
        }
        return null;
    }

    public CompletableFuture<BaseResponse> getBaseResponseCompletableFuture(HttpRequest request) {
        return client.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenApply(HttpResponse::body)
                .thenApply(WebClientHandler::mapToBaseResponse);
    }

    private static BaseResponse mapToBaseResponse(String x) {
        try {
            return getMapper().readValue(x, BaseResponse.class);
        } catch (JsonProcessingException e) {
            throw new ProcessingException(e.getMessage(), e);
        }
    }

    public ValidateAccountResponse processValidateAccountResponse(ValidateAccountDto validateAccountDto, HttpRequest request, String bankName) {
        return getBaseResponseCompletableFuture(request)
                .thenApply(body -> {
                    if (body.getData() != null) {
                        var res = gson.fromJson(gson.toJson(body.getData()), ValidateAccountResponse.class);
                        res.setBank_code(validateAccountDto.getCode());
                        res.setBank_name(bankName);
                        return res;
                    } else {
                        throw new InvalidRequestException(body.getMessage());
                    }
                }).join();
    }

    public String processFieldValue(HttpRequest request, String field){
        return getBaseResponseCompletableFuture(request)
                .thenApply(responseBody -> {
                    if (responseBody.getData() != null) {
                        JsonObject data = gson.toJsonTree(responseBody.getData()).getAsJsonObject();
                        log.info("JsonObject data: {}", data);
                        return data.get(field).getAsString();
                    } else {
                        return responseBody.getMessage();
                    }
                }).join();

    }
}
