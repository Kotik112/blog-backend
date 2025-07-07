package com.example.blogbackend.domain;

import com.example.blogbackend.dto.ContactRequestDto;
import com.example.blogbackend.enums.ContactStatus;
import jakarta.persistence.*;
import java.time.Instant;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Entity
@Table(name = "contact_us_tbl")
public class Contact {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private long id;

  private String name;
  private String email;
  private String message;
  private Instant createdAt;
  private Instant updatedAt;

  @Enumerated(EnumType.STRING)
  private ContactStatus status;

  private String userAgent;
  private String internalNote;

  @PrePersist
  public void prePersist() {
    Instant now = Instant.now();
    this.createdAt = now;
    this.updatedAt = now;
    this.status = ContactStatus.PENDING;
  }

  @PreUpdate
  public void preUpdate() {
    this.updatedAt = Instant.now();
  }

  public static Contact from(ContactRequestDto contactRequestDto) {
    Contact contact = new Contact();
    contact.setName(contactRequestDto.getName());
    contact.setEmail(contactRequestDto.getEmail());
    contact.setMessage(contactRequestDto.getMessage());
    return contact;
  }
}
