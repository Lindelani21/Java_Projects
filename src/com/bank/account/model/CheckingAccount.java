package com.bank.account.model;

import com.bank.account.exceptions.InsufficientFundsException;
import com.bank.account.exceptions.InvalidAmountException;
import java.io.Serializable;

public class CheckingAccount extends Account implements Serializable {
	private double overdraftLimit;

    public CheckingAccount(String accountNumber, String accountHolder,
                         String pin, double initialBalance, double overdraftLimit) {
        super(accountNumber, accountHolder, pin, initialBalance);
        this.overdraftLimit = overdraftLimit;
    }

    @Override
    public void withdraw(double amount) throws InsufficientFundsException, InvalidAmountException {
        if (amount <= 0) {
            throw new InvalidAmountException("Withdrawal amount must be positive");
        }
        if (amount > (getBalance() + overdraftLimit)) {
            throw new InsufficientFundsException(
                "Withdrawal exceeds available balance and overdraft limit");
        }
        super.balance -= amount;
        getTransactions().add(new Transaction("Withdrawal", -amount, getBalance()));
    }

    public double getOverdraftLimit() {
        return overdraftLimit;
    }

    public void setOverdraftLimit(double overdraftLimit) {
        this.overdraftLimit = overdraftLimit;
    }
}