package com.codewithfibbee.ipay.util;

import lombok.Data;
import org.springframework.http.HttpStatusCode;

@Data
//@RequiredArgsConstructor
public class ErrorResponseEntity {
    int code;
    String description;

    public ErrorResponseEntity(HttpStatusCode code) {
        this.code = code.value();
    }
}
