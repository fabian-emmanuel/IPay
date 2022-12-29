package com.codewithfibbee.ipay.apiresponse;

import lombok.Data;
import org.springframework.http.HttpStatus;

@Data
public class ApiDataResponse<T> {
  private HttpStatus status;
  private String message;
  private T content;

  public ApiDataResponse(HttpStatus status) {
    this.status = status;
  }
}