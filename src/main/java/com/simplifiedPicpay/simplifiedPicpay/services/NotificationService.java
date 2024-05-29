package com.simplifiedPicpay.simplifiedPicpay.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.simplifiedPicpay.simplifiedPicpay.domain.user.User;
import com.simplifiedPicpay.simplifiedPicpay.dtos.NotificationDTO;

@Service
public class NotificationService {
  @Autowired
  private RestTemplate restTemplate;

  public void sendNotification(User user, String message) throws Exception {
    String email = user.getEmail();
    NotificationDTO notificationRequest = new NotificationDTO(email, message);

    ResponseEntity<String> notificationResponse = this.restTemplate.postForEntity(
        "https://util.devi.tools/api/v1/notify",
        notificationRequest, String.class);

    if (!(notificationResponse.getStatusCode() == HttpStatus.OK)) {
      System.out.println("Error sending notification");
      throw new Exception("Notification service is offline");
    }

  }
}
