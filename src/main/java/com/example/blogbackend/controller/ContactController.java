package com.example.blogbackend.controller;

import com.example.blogbackend.dto.ContactRequestDto;
import com.example.blogbackend.service.MailService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@SuppressWarnings("unused")
@RestController
@RequestMapping("/api/v1/contact")
public class ContactController {

  private final MailService mailService;

  public ContactController(MailService mailService) {
    this.mailService = mailService;
  }

  @PostMapping
  public ResponseEntity<String> handleContactSubmission(
      @Valid @RequestBody ContactRequestDto dto,
      @RequestHeader(value = "User-Agent", required = false) String userAgent) {
    String responseMessage = mailService.saveContactToDB(dto, userAgent);
    return ResponseEntity.ok(responseMessage);
  }
}
