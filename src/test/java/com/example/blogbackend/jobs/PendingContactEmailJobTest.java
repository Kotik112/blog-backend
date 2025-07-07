package com.example.blogbackend.jobs;

import static org.mockito.Mockito.verify;

import com.example.blogbackend.service.MailService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class PendingContactEmailJobTest {

  @Mock MailService mailService;

  @InjectMocks PendingContactEmailJob pendingContactEmailJob;

  @Test
  void testSendPendingContactEmails() {

    pendingContactEmailJob.sendPendingContactEmails();

    verify(mailService).processPendingContactEmails();
  }
}
