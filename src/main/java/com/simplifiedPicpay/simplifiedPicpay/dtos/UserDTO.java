package com.simplifiedPicpay.simplifiedPicpay.dtos;

import java.math.BigDecimal;

import com.simplifiedPicpay.simplifiedPicpay.domain.user.UserType;

public record UserDTO(String firstName, String lastName, String document, BigDecimal balance, String email, String password, UserType userType) {

}
