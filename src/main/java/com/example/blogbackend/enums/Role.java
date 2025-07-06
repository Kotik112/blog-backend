package com.example.blogbackend.enums;

import lombok.Getter;

@Getter
public enum Role {
  USER,
  ADMIN;

  public String getAuthority() {
    return "ROLE_" + this.name();
  }
}
