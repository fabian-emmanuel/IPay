package com.codewithfibbee.ipay.config;

import com.codewithfibbee.ipay.exceptions.ProcessingException;
import com.codewithfibbee.ipay.payloads.response.BaseResponse;
import com.codewithfibbee.ipay.payloads.response.ListBanksResponse;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.google.gson.Gson;
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
}
