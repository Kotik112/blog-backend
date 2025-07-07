package com.example.blogbackend.jobs;

import com.example.blogbackend.service.MailService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@SuppressWarnings("unused")
@Component
public class PendingContactEmailJob {

  private final Logger logger = LoggerFactory.getLogger(PendingContactEmailJob.class);
  private final MailService mailService;

  public PendingContactEmailJob(MailService mailService) {
    this.mailService = mailService;
  }

  @Scheduled(cron = "0 */30 * * * ?")
  public void sendPendingContactEmails() {
    mailService.processPendingContactEmails();
  }
}
