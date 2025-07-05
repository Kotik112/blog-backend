package com.example.blogbackend.dto;

import com.example.blogbackend.enums.LoginResponseEnum;

/**
 * Represents the response from an API login operation. Contains a message indicating the result of
 * the login operation.
 *
 * @param responseEnum The enum representing the login response status.
 */
public record ApiLoginResponse(LoginResponseEnum responseEnum) {

  public String message() {
    return responseEnum.getMessage();
  }
}
