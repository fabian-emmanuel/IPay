package com.codewithfibbee.ipay.apiresponse;

import lombok.Data;
import org.springframework.http.HttpStatus;

@Data
public class ApiResponse<T> {
  private HttpStatus status;
  private String message;
  private T content;

  public ApiResponse(HttpStatus status) {
    this.status = status;
  }
}