package com.bank.account.service;

import com.bank.account.model.*;
import com.bank.account.exceptions.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Bank {
    private List<Account> accounts;
    private int nextAccountNumber = 1001;
    

    public Bank() {
        this.accounts = new ArrayList<>();
    }

    public String generateAccountNumber() {
        return "ACCT" + nextAccountNumber++;
    }

    public void addAccount(Account account) {
        accounts.add(account);
    }

    public Account findAccount(String accountNumber) throws AccountNotFoundException {
        return accounts.stream()
            .filter(account -> account.getAccountNumber().equals(accountNumber))
            .findFirst()
            .orElseThrow(() -> new AccountNotFoundException("Account not found: " + accountNumber));
    }

    public List<Account> findAccountsByHolder(String holderName) {
        return accounts.stream()
            .filter(account -> account.getAccountHolder().equalsIgnoreCase(holderName))
            .collect(Collectors.toList());
    }
    
    public Account recoverAccount(String accountNumber, String fullName) throws AccountNotFoundException {
        return accounts.stream()
            .filter(acc -> acc.getAccountNumber().equals(accountNumber) && 
                          acc.getAccountHolder().equalsIgnoreCase(fullName))
            .findFirst()
            .orElseThrow(() -> new AccountNotFoundException("Account recovery failed - details don't match"));
    }

    public void createSavingsAccount(String accountHolder, String pin, double initialBalance, double interestRate) {
		String accountNumber = generateAccountNumber();
		SavingsAccount account = new SavingsAccount(
		accountNumber, accountHolder, pin, initialBalance, interestRate);
		accounts.add(account);
    }

    public void createCheckingAccount(String accountHolder, String pin, double initialBalance, double overdraftLimit) {
		String accountNumber = generateAccountNumber();
		CheckingAccount account = new CheckingAccount(accountNumber, accountHolder, pin, initialBalance, overdraftLimit);
		accounts.add(account);
    }
    

    public void setNextAccountNumber(int nextAccountNumber) {
        this.nextAccountNumber = nextAccountNumber;
    }

    public int getNextAccountNumber() {
        return nextAccountNumber;
    }

    public double getTotalBankAssets() {
        return accounts.stream()
            .mapToDouble(Account::getBalance)
            .sum();
    }

    public List<Account> getAllAccounts() {
        return new ArrayList<>(accounts);
    }

    public void applyMonthlyInterestToAllSavingsAccounts() {
        accounts.stream()
            .filter(account -> account instanceof SavingsAccount)
            .forEach(account -> ((SavingsAccount)account).applyInterest());
    }

    public void closeAccount(String accountNumber) throws AccountNotFoundException {
        Account account = findAccount(accountNumber);
        if (account.getBalance() != 0) {
            throw new IllegalStateException("Cannot close account with non-zero balance");
        }
        accounts.remove(account);
    }
}