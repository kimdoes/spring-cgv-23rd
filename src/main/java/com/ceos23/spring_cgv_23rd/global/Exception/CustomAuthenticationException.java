package com.ceos23.spring_cgv_23rd.global.Exception;

public class CustomAuthenticationException extends RuntimeException {
  public CustomAuthenticationException(String message) {
    super(message);
  }
}
