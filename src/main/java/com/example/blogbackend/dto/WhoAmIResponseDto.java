package com.example.blogbackend.dto;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class WhoAmIResponseDto {
  private String username;
  private String ipAddress;
  private List<String> roles;
  private String sessionId;
}
