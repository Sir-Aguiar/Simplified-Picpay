package com.simplifiedPicpay.simplifiedPicpay.services;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.simplifiedPicpay.simplifiedPicpay.domain.transaction.Transaction;
import com.simplifiedPicpay.simplifiedPicpay.domain.user.User;
import com.simplifiedPicpay.simplifiedPicpay.dtos.TransactionDTO;
import com.simplifiedPicpay.simplifiedPicpay.repositories.TransactionRepository;

@Service
public class TransactionService {

  @Autowired
  private UserService userService;

  @Autowired
  private TransactionRepository transactionRepository;

  @Autowired
  private NotificationService notificationService;

  @Autowired
  private RestTemplate restTemplate;

  public Transaction createTransaction(TransactionDTO transaction) throws Exception {
    User sender = this.userService.findUserById(transaction.senderId());
    User receiver = this.userService.findUserById(transaction.receiverId());

    this.userService.validateTransaction(sender, transaction.amount());

    Boolean isAuthorized = this.authorizeTransaction(sender, transaction.amount());

    if (!isAuthorized) {
      throw new Exception("Unauthorized transaction");
    }

    Transaction newTransaction = new Transaction();

    newTransaction.setAmount(transaction.amount());
    newTransaction.setSender(sender);
    newTransaction.setReceiver(receiver);
    newTransaction.setTimestamp(LocalDateTime.now());

    sender.setBalance(sender.getBalance().subtract(transaction.amount()));
    receiver.setBalance(receiver.getBalance().add(transaction.amount()));

    this.transactionRepository.save(newTransaction);
    this.userService.saveUser(sender);
    this.userService.saveUser(receiver);

    this.notificationService.sendNotification(sender, "Successfully created a transaction");
    this.notificationService.sendNotification(receiver, "Successfully received a transaction");

    return newTransaction;
  }

  public boolean authorizeTransaction(User sender, BigDecimal amount) throws Exception {
    ResponseEntity<Map> response = this.restTemplate.getForEntity("https://util.devi.tools/api/v2/authorize",
        Map.class);
    if (response.getStatusCode() == HttpStatus.OK) {
      String message = (String) response.getBody().get("status");
      return "success".equalsIgnoreCase(message);
    } else {
      return false;
    }
  }
}
