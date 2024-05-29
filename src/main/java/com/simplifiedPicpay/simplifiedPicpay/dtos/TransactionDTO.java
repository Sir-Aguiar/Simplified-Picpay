package com.simplifiedPicpay.simplifiedPicpay.dtos;

import java.math.BigDecimal;

public record TransactionDTO(BigDecimal amount, Long senderId, Long receiverId) {

}
