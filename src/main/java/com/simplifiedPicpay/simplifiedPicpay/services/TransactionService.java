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
  private RestTemplate restTemplate;

  public void createTransaction(TransactionDTO transaction) throws Exception {
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
  }

  public boolean authorizeTransaction(User sender, BigDecimal amount) throws Exception {
    ResponseEntity<Map> response = this.restTemplate.getForEntity("https://util.devi.tools/api/v2/authorize",
        Map.class);

    return response.getStatusCode() == HttpStatus.OK;
  }
}
