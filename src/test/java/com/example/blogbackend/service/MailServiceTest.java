package com.example.blogbackend.service;

import static org.mockito.Mockito.*;

import com.example.blogbackend.domain.Contact;
import com.example.blogbackend.enums.ContactStatus;
import com.example.blogbackend.provider.TimeProvider;
import com.example.blogbackend.repository.ContactRepository;
import java.time.Instant;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.javamail.JavaMailSender;

@ExtendWith(MockitoExtension.class)
class MailServiceTest {

  @Mock TimeProvider timeProvider;

  @Mock ContactRepository contactRepository;

  @Mock JavaMailSender javaMailSender;

  @InjectMocks MailService mailService;

  @Test
  void testProcessPendingContactEmails() {
    Instant now = Instant.now();
    List<Contact> contacts =
        List.of(
            new Contact(
                1L,
                "John Doe",
                "test@example.com",
                "Hello, this is a test message.",
                now,
                now,
                ContactStatus.PENDING,
                null,
                null),
            new Contact(
                2L,
                "Jane Smith",
                "test@gmail.com",
                "Another test message.",
                now,
                now,
                ContactStatus.PENDING,
                null,
                null));

    when(timeProvider.getNow()).thenReturn(now);
    when(contactRepository.findByStatus(ContactStatus.PENDING)).thenReturn(contacts);

    mailService.processPendingContactEmails();

    // Verify that the contactRepository methods were called correctly
    verify(contactRepository, times(1)).findByStatus(ContactStatus.PENDING);
    verify(contactRepository, times(1)).updateStatusById(List.of(1L, 2L), ContactStatus.SENT, now);
  }

  @Test
  void testProcessPendingContactEmailsWithNoPendingContacts() {
    when(contactRepository.findByStatus(ContactStatus.PENDING)).thenReturn(List.of());
    mailService.processPendingContactEmails();

    // Verify that no updates were made when there are no pending contacts
    verify(contactRepository, times(1)).findByStatus(ContactStatus.PENDING);
    verify(contactRepository, never()).updateStatusById(anyList(), any(), any());
  }
}
