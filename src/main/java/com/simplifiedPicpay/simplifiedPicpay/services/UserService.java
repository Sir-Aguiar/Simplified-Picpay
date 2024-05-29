package com.simplifiedPicpay.simplifiedPicpay.services;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.simplifiedPicpay.simplifiedPicpay.domain.user.User;
import com.simplifiedPicpay.simplifiedPicpay.domain.user.UserType;
import com.simplifiedPicpay.simplifiedPicpay.dtos.UserDTO;
import com.simplifiedPicpay.simplifiedPicpay.repositories.UserRepository;

@Service
public class UserService {
  @Autowired
  private UserRepository userRepository;

  public void validateTransaction(User sender, BigDecimal amount) throws Exception {
    if (sender.getUserType() == UserType.MERCHANT) {
      throw new Exception("Merchant users are not allowed to create transactions");
    }

    if (sender.getBalance().compareTo(amount) < 0) {
      throw new Exception("User balance is insufficient for this transaction");
    }
  }

  public User createUser(UserDTO data) {
    User newUser = new User(data);
    this.saveUser(newUser);
    return newUser;
  }

  public List<User> getAllUsers() {
    return this.userRepository.findAll();
  }

  public User findUserById(Long id) throws Exception {
    return this.userRepository.findUserById(id).orElseThrow(() -> new Exception("User not found"));
  }

  public void saveUser(User user) {
    this.userRepository.save(user);
  }
}
