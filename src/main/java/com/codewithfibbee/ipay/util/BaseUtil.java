package com.codewithfibbee.ipay.util;

import com.codewithfibbee.ipay.enums.Provider;
import com.codewithfibbee.ipay.exceptions.InvalidRequestException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.Objects;
import java.util.UUID;

public class BaseUtil {
    public static String generateUniqueRef(){
        return UUID.randomUUID().toString();
    }

    public static String validateProvider(String provider) {

        if(Objects.equals(provider, Provider.FlutterWave.toString())){
            provider = "FlutterWaveProvider";
        } else if (Objects.equals(provider, Provider.PayStack.toString())){
            provider = "PayStackProvider";
        } else {
            throw new InvalidRequestException("Invalid Provider");
        }
        return provider;
    }

    public static ObjectMapper getMapper() {
        return new ObjectMapper();
    }

    public static String convertToJsonBody(Object object) {
        try {
            return getMapper()
                    .writerWithDefaultPrettyPrinter()
                    .writeValueAsString(object);
        } catch (Exception e) {
            throw new InvalidRequestException(e.getMessage());
        }
    }

    public static String[] getAuthHeader(String auth){
        return new String[]{"Authorization", String.format("Bearer %s", auth), "Content-Type", "application/json"};
    }
}
