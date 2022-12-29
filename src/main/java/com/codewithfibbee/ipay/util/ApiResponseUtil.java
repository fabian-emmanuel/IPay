package com.codewithfibbee.ipay.util;

import com.codewithfibbee.ipay.apiresponse.ApiDataResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class ApiResponseUtil {

    public static  <T> ResponseEntity<ApiDataResponse<T>> response(HttpStatus status, T content, String message ){
        return ApiResponseUtil.getResponse(status,content,message);
    }

    private static  <T> ResponseEntity<ApiDataResponse<T>> getResponse(HttpStatus status, T content, String message ){
        ApiDataResponse<T> ar = new ApiDataResponse<>(status);
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
