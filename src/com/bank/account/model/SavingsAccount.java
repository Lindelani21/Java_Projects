package com.bank.account.model;

import com.bank.account.exceptions.InsufficientFundsException;
import com.bank.account.exceptions.InvalidAmountException;
import java.io.Serializable;

public class SavingsAccount extends Account implements Serializable {
	private double interestRate;

    public SavingsAccount(String accountNumber, String accountHolder, 
                        String pin, double initialBalance, double interestRate) {
        super(accountNumber, accountHolder, pin, initialBalance);
        this.interestRate = interestRate;
    }

    @Override
    public void withdraw(double amount) throws InsufficientFundsException, InvalidAmountException {
        if (amount <= 0) {
            throw new InvalidAmountException("Withdrawal amount must be positive");
        }
        if (amount > getBalance()) {
            throw new InsufficientFundsException("Insufficient funds for withdrawal");
        }
        super.balance -= amount;
        getTransactions().add(new Transaction("Withdrawal", -amount, getBalance()));
    }

    public void applyInterest() {
        double interest = getBalance() * interestRate / 100;
        super.balance += interest;
        getTransactions().add(new Transaction("Interest Applied", interest, getBalance()));
    }

    public double getInterestRate() {
        return interestRate;
    }

    public void setInterestRate(double interestRate) {
        this.interestRate = interestRate;
    }
}