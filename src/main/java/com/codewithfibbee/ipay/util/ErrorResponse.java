package com.codewithfibbee.ipay.util;

import lombok.Data;

@Data
public class ErrorResponse {
    int code = 96;
    String description;

    public ErrorResponse(String description) {
        this.description = description;
    }
}
