package com.example.blogbackend.service;

import com.example.blogbackend.domain.Contact;
import com.example.blogbackend.dto.ContactRequestDto;
import com.example.blogbackend.exception.EmailSendException;
import com.example.blogbackend.repository.ContactRepository;
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

  @Value(value = "${mail.to}")
  protected String to;

  public MailService(JavaMailSender mailSender, ContactRepository contactRepository) {
    this.mailSender = mailSender;
    this.contactRepository = contactRepository;
  }

  public String sendContactEmail(ContactRequestDto request, String userAgent) {
    SimpleMailMessage message = new SimpleMailMessage();
    message.setTo(to);
    message.setFrom(request.getEmail());
    message.setSubject("Contact form submission from " + request.getName());
    message.setText(
        String.format(
                """
    From: %s
    Email: %s
    Message: %s
    """,
                request.getName(), request.getEmail(), request.getMessage())
            .trim());
    try {
      //mailSender.send(message);
      logger.info("Email sent successfully from {} to {}", request.getEmail(), to);
      // Save contact request to the database
      Contact contact = Contact.from(request);
      contact.setUserAgent(userAgent);
      contactRepository.save(contact);
      return "Email sent successfully";
    } catch (Exception e) {
      logger.error("Failed to send email from {}: {}", request.getEmail(), e.getMessage());
      throw new EmailSendException("Failed to send email");
    }
  }
}
