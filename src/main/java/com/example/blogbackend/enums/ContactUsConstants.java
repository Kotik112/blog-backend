package com.example.blogbackend.enums;

public enum ContactUsConstants {
  DEFAULT_SUBJECT("New Contact Form Submission"),
  ;

  private final String value;

  ContactUsConstants(String value) {
    this.value = value;
  }

  public String getValue() {
    return value;
  }
}
