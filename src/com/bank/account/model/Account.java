package com.bank.account.model;

import com.bank.account.exceptions.*;
import java.util.ArrayList;
import java.util.List;
import java.io.Serializable;

public abstract class Account  implements Serializable {
    private String accountNumber;
    private String accountHolder;
    private String pin;
    protected double balance;
    private List<Transaction> transactions;

    public Account(String accountNumber, String accountHolder, String pin, double initialBalance) {
        this.accountNumber = accountNumber;
        this.accountHolder = accountHolder;
        this.pin = pin;
        this.balance = initialBalance;
        this.transactions = new ArrayList<>();
        this.transactions.add(new Transaction("Initial deposit", initialBalance, initialBalance));
    }

    public void deposit(double amount) throws InvalidAmountException {
        if (amount <= 0) {
            throw new InvalidAmountException("Deposit amount must be positive");
        }
        balance += amount;
        transactions.add(new Transaction("Deposit", amount, balance));
    }
    
    public boolean validatePin(String pin) {
        return this.pin.equals(pin);
    }

    public void changePin(String oldPin, String newPin) throws InvalidPinException {
        if (!validatePin(oldPin)) {
            throw new InvalidPinException("Incorrect current PIN");
        }
        if (newPin.length() != 4 || !newPin.matches("\\d+")) {
            throw new InvalidPinException("PIN must be 4 digits");
        }
        this.pin = newPin;
    }
    
    public String getPin() {
        return pin;
    }

    public abstract void withdraw(double amount) throws InsufficientFundsException, InvalidAmountException;

    public void displayBalance() {
        System.out.printf("Account Balance: $%.2f%n", balance);
    }

    public void displayTransactions() {
        System.out.println("\nTransaction History:");
        System.out.println("---------------------------------");
        for (Transaction t : transactions) {
            System.out.println(t);
        }
        System.out.println("---------------------------------");
    }

    public void transfer(Account recipient, double amount) 
            throws InsufficientFundsException, InvalidAmountException {
        this.withdraw(amount);
        recipient.deposit(amount);
        transactions.add(new Transaction(
            "Transfer to " + recipient.getAccountNumber(), -amount, balance));
        recipient.transactions.add(new Transaction(
            "Transfer from " + this.accountNumber, amount, recipient.getBalance()));
    }

    // Getters
    public String getAccountNumber() { return accountNumber; }
    public String getAccountHolder() { return accountHolder; }
    public double getBalance() { return balance; }
    public List<Transaction> getTransactions() { return transactions; }
}