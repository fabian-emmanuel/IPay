package com.codewithfibbee.ipay.payloads.response;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class BaseResponse<T> {
    private String status;
    private String message;
    private T data;
}
