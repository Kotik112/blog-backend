package com.example.blogbackend.service;

import com.example.blogbackend.domain.Contact;
import com.example.blogbackend.dto.ContactRequestDto;
import com.example.blogbackend.enums.ContactStatus;
import com.example.blogbackend.exception.EmailSendException;
import com.example.blogbackend.provider.TimeProvider;
import com.example.blogbackend.repository.ContactRepository;
import jakarta.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class MailService {

  private final Logger logger = LoggerFactory.getLogger(MailService.class);

  private final JavaMailSender mailSender;
  private final ContactRepository contactRepository;
  public final TimeProvider timeProvider;

  @Value(value = "${mail.to}")
  protected String to;

  public MailService(
      JavaMailSender mailSender, ContactRepository contactRepository, TimeProvider timeProvider) {
    this.mailSender = mailSender;
    this.contactRepository = contactRepository;
    this.timeProvider = timeProvider;
  }

  @Transactional
  public void processPendingContactEmails() {
    List<Contact> pendingContacts = contactRepository.findByStatus(ContactStatus.PENDING);
    if (pendingContacts.isEmpty()) {
      logger.info("No pending contact emails to process.");
      return;
    }
    logger.info("Processing {} pending contact emails.", pendingContacts.size());

    List<Long> sendIds = new ArrayList<>();
    List<Long> failedIds = new ArrayList<>();
    for (Contact contact : pendingContacts) {
      try {
        sendEmail(contact);
        sendIds.add(contact.getId());
      } catch (EmailSendException e) {
        logger.error("Error sending email for contact {}: {}", contact.getId(), e.getMessage());
        failedIds.add(contact.getId());
      }
    }
    if (!sendIds.isEmpty()) {
      contactRepository.updateStatusById(sendIds, ContactStatus.SENT, timeProvider.getNow());
      logger.info("Successfully sent {} emails for contacts: {}", sendIds.size(), sendIds);
    }
    if (!failedIds.isEmpty()) {
      contactRepository.updateStatusById(failedIds, ContactStatus.FAILED, timeProvider.getNow());
      logger.error("Failed to send {} emails for contacts: {}", failedIds.size(), failedIds);
    }
    if (sendIds.size() + failedIds.size() != pendingContacts.size()) {
      logger.warn(
          "Some contacts were not processed. Total contacts: {}, Sent: {}, Failed: {}",
          pendingContacts.size(),
          sendIds.size(),
          failedIds.size());
    } else {
      logger.info("All pending contact emails processed successfully.");
    }
  }

  /**
   * Sends an email using the provided contact information.
   *
   * @param contact the contact information containing name, email, and message
   * @throws EmailSendException if there is an error sending the email
   */
  protected void sendEmail(Contact contact) {
    SimpleMailMessage message = new SimpleMailMessage();
    message.setTo(to);
    message.setFrom(contact.getEmail());
    message.setSubject("Contact form submission from " + contact.getName());
    message.setText(
        String.format(
                """
    From: %s
    Email: %s
    Message: %s
    """,
                contact.getName(), contact.getEmail(), contact.getMessage())
            .trim());

    try {
      mailSender.send(message);
      logger.info("Email sent successfully from {} to {}", contact.getEmail(), to);
      contact.setStatus(ContactStatus.SENT);
      contact.setUpdatedAt(timeProvider.getNow());
      contactRepository.save(contact);
    } catch (Exception e) {
      logger.error("Failed to send email from {}: {}", contact.getEmail(), e.getMessage());
      throw new EmailSendException("Failed to send email");
    }
  }

  /**
   * Sends a contact email with the details provided in the ContactRequestDto.
   *
   * @param request the contact request containing name, email, and message
   * @param userAgent the user agent string from the request header
   * @return a success message if the email is sent successfully
   * @throws EmailSendException if there is an error sending the email
   */
  public String saveContactToDB(ContactRequestDto request, String userAgent) {
    Contact contact = Contact.from(request);
    contact.setUserAgent(userAgent);
    contact.setCreatedAt(timeProvider.getNow());
    contactRepository.save(contact);
    logger.info("Contact saved to database with ID: {}", contact.getId());
    return "Contact request submitted successfully. We will get back to you soon.";
  }
}
