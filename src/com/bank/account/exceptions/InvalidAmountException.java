package com.bank.account.exceptions;

public class InvalidAmountException extends Exception {
    public InvalidAmountException() {
        super("Invalid amount in your account to complete this transaction.");
    }

    public InvalidAmountException(String message) {
        super(message);
    }
}