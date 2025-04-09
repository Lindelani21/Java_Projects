package com.bank.account.model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.io.Serializable;

public class Transaction implements Serializable {
    private String description;
    private double amount;
    private double balanceAfter;
    private LocalDateTime timestamp;

    public Transaction(String description, double amount, double balanceAfter) {
        this.description = description;
        this.amount = amount;
        this.balanceAfter = balanceAfter;
        this.timestamp = LocalDateTime.now();
    }

    @Override
    public String toString() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String formattedTime = timestamp.format(formatter);
        return String.format("[%s] %s: $%.2f | Balance: $%.2f", 
                formattedTime, description, amount, balanceAfter);
    }
}