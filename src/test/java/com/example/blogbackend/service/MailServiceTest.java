package com.example.blogbackend.service;

import static org.mockito.Mockito.*;

import com.example.blogbackend.domain.Contact;
import com.example.blogbackend.enums.ContactStatus;
import com.example.blogbackend.provider.TimeProvider;
import com.example.blogbackend.repository.ContactRepository;
import java.time.Instant;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.MailSendException;
import org.springframework.mail.SimpleMailMessage;
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

  @Test
  void testProcessPendingContactEmails_AllFail() {
    Instant now = Instant.now();
    List<Contact> contacts =
        List.of(
            new Contact(
                1L, "John", "john@example.com", "Msg", now, now, ContactStatus.PENDING, null, null),
            new Contact(
                2L,
                "Jane",
                "jane@example.com",
                "Msg",
                now,
                now,
                ContactStatus.PENDING,
                null,
                null));

    when(timeProvider.getNow()).thenReturn(now);
    when(contactRepository.findByStatus(ContactStatus.PENDING)).thenReturn(contacts);
    doThrow(new MailSendException("error")).when(javaMailSender).send(any(SimpleMailMessage.class));

    mailService.processPendingContactEmails();

    verify(contactRepository, times(1))
        .updateStatusById(List.of(1L, 2L), ContactStatus.FAILED, now);
  }

  @Test
  void testProcessPendingContactEmails_PartialSuccess() {
    Instant now = Instant.now();
    Contact successContact =
        new Contact(
            1L, "John", "john@example.com", "Msg", now, now, ContactStatus.PENDING, null, null);
    Contact failContact =
        new Contact(
            2L, "Jane", "jane@example.com", "Msg", now, now, ContactStatus.PENDING, null, null);
    List<Contact> contacts = List.of(successContact, failContact);

    when(timeProvider.getNow()).thenReturn(now);
    when(contactRepository.findByStatus(ContactStatus.PENDING)).thenReturn(contacts);

    doAnswer(
            invocation -> {
              SimpleMailMessage msg = invocation.getArgument(0);
              Assertions.assertNotNull(msg.getFrom());
              if (msg.getFrom().equals("jane@example.com")) {
                throw new MailSendException("fail");
              }
              return null;
            })
        .when(javaMailSender)
        .send(any(SimpleMailMessage.class));

    mailService.processPendingContactEmails();

    verify(contactRepository).updateStatusById(List.of(1L), ContactStatus.SENT, now);
    verify(contactRepository).updateStatusById(List.of(2L), ContactStatus.FAILED, now);
  }

  @Test
  void testProcessPendingContactEmails_SomeUnprocessed() {
    Instant now = Instant.now();
    Contact contact1 =
        new Contact(
            1L, "John", "john@example.com", "Msg", now, now, ContactStatus.PENDING, null, null);
    Contact contact2 =
        new Contact(
            2L, "Jane", "jane@example.com", "Msg", now, now, ContactStatus.PENDING, null, null);

    List<Contact> contacts = List.of(contact1, contact2);
    when(timeProvider.getNow()).thenReturn(now);
    when(contactRepository.findByStatus(ContactStatus.PENDING)).thenReturn(contacts);

    doAnswer(
            invocation -> {
              SimpleMailMessage msg = invocation.getArgument(0);
                Assertions.assertNotNull(msg.getFrom());
                if (msg.getFrom().equals("jane@example.com")) {
                throw new NullPointerException("Unexpected failure");
              }
              return null;
            })
        .when(javaMailSender)
        .send(any(SimpleMailMessage.class));

    mailService.processPendingContactEmails();

    // Both should be processed (1 SENT, 2 FAILED)
    verify(contactRepository).updateStatusById(List.of(1L), ContactStatus.SENT, now);
    verify(contactRepository).updateStatusById(List.of(2L), ContactStatus.FAILED, now);
  }
}
