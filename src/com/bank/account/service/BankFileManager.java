package com.bank.account.service;

import com.bank.account.model.*;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class BankFileManager {
    private static final String DATA_FILE = "data/bank_data.ser";

    public static void saveBank(Bank bank) throws IOException {
        File dataDir = new File("data");
        if (!dataDir.exists()) {
            dataDir.mkdir();
        }
        
        try (ObjectOutputStream oos = new ObjectOutputStream(
                new FileOutputStream(DATA_FILE))) {
            oos.writeObject(bank.getAllAccounts());
            oos.writeInt(bank.getNextAccountNumber());
        }
    }

    public static Bank loadBank() throws IOException, ClassNotFoundException {
        File file = new File(DATA_FILE);
        if (!file.exists()) {
            return new Bank(); // Return new bank if no data file exists
        }

        try (ObjectInputStream ois = new ObjectInputStream(
                new FileInputStream(DATA_FILE))) {
            @SuppressWarnings("unchecked")
            List<Account> accounts = (List<Account>) ois.readObject();
            int nextAccountNumber = ois.readInt();
            
            Bank bank = new Bank();
            accounts.forEach(bank::addAccount);
            bank.setNextAccountNumber(nextAccountNumber);
            return bank;
        }
    }
}