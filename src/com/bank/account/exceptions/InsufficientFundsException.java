package com.bank.account.exceptions;

public class InsufficientFundsException extends Exception {
    public InsufficientFundsException() {
        super("Insufficient funds in your account to complete this transaction.");
    }

    public InsufficientFundsException(String message) {
        super(message);
    }
}