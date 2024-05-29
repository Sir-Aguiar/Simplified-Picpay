package com.simplifiedPicpay.simplifiedPicpay.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.simplifiedPicpay.simplifiedPicpay.domain.transaction.Transaction;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {

}
