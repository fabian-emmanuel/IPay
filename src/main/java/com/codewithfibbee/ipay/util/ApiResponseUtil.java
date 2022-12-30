package com.codewithfibbee.ipay.util;

import com.codewithfibbee.ipay.apiresponse.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class ApiResponseUtil {

    public static  <T> ResponseEntity<ApiResponse<T>> response(HttpStatus status, T content, String message ){
        return ApiResponseUtil.getResponse(status,content,message);
    }

    private static  <T> ResponseEntity<ApiResponse<T>> getResponse(HttpStatus status, T content, String message ){
        ApiResponse<T> ar = new ApiResponse<>(status);
        ar.setContent(content);
        ar.setMessage(message);
        return new ResponseEntity<>(ar,status);
    }

    public static <T> ResponseEntity<ErrorResponseEntity> errorResponse(HttpStatus status, String errMsg){
        ErrorResponseEntity er = new ErrorResponseEntity(status);
        er.setDescription(errMsg);
        return new ResponseEntity<>(er, status);
    }


}
