package com.bank.account;

import com.bank.account.model.*;
import com.bank.account.service.*;
import com.bank.account.exceptions.*;
import java.io.IOException;
import java.util.Scanner;

public class BankApp {
    private static Bank bank = new Bank();
    private static Account currentAccount;
    private static Scanner scanner = new Scanner(System.in);
    private static boolean isAdmin = false;

    public static void main(String[] args) {
    	initializeBank();
        showWelcomeMenu();
    }
    
    private static void initializeBank() {
        try {
            bank = BankFileManager.loadBank();
            System.out.println("Bank data loaded successfully");
            
            if (bank.getAllAccounts().isEmpty() && isDevelopmentMode()) {
                initializeSampleData();
            }
        } catch (IOException | ClassNotFoundException ex) {
            System.out.println("Error loading bank data. Starting with new bank.");
            bank = new Bank();
            initializeSampleData(); // Only if you want sample data
        }
    }

    private static boolean isDevelopmentMode() {
        return System.getProperty("bank.dev.mode") != null;
    }

    private static void initializeSampleData() {
        if (bank.getAllAccounts().isEmpty()) {
            try {
                bank.createSavingsAccount("John Doe", "1234", 1000, 1.5);
                bank.createCheckingAccount("Jane Smith", "5678", 2000, 500);
                bank.createSavingsAccount("Admin User", "0000", 5000, 2.0);
                System.out.println("Sample accounts initialized with PINs:");
                System.out.println("John Doe (Savings): PIN 1234");
                System.out.println("Jane Smith (Checking): PIN 5678");
                System.out.println("Admin User (Savings): PIN 0000");
            } catch (Exception ex) {
                System.out.println("Error initializing sample data: " + ex.getMessage());
            }
        }
    }
    
    private static void saveBankData() {
        try {
            BankFileManager.saveBank(bank);
            System.out.println("Bank data saved successfully");
        } catch (IOException ex) {
            System.out.println("Error saving bank data: " + ex.getMessage());
        }
    }

    private static void showWelcomeMenu() {
        while (true) {
            System.out.println("\n=== BANK MANAGEMENT SYSTEM ===");
            System.out.println("1. Customer Login");
            System.out.println("2. Create New Account");
            System.out.println("3. Admin Login");
            System.out.println("4. Exit");
            System.out.print("Enter your choice: ");
            
            int choice = scanner.nextInt();
            scanner.nextLine();
            
            switch (choice) {
                case 1:
                    customerLogin();
                    break;
                case 2:
                    createAccount();
                    break;
                case 3:
                    adminLogin();
                    break;
                case 4:
                    saveBankData();
                    System.out.println("Thank you for using our banking system!");
                    System.exit(0);
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        }
    }


    private static void adminLogin() {
        System.out.print("\nEnter admin password: ");
        String password = scanner.nextLine();
        
        // Simple password check (in real app, use proper authentication)
        if ("admin123".equals(password)) {
            isAdmin = true;
            currentAccount = bank.findAccountsByHolder("Admin User").get(0);
            showAdminMenu();
        } else {
            System.out.println("Invalid admin credentials!");
        }
    }

    private static void showAdminMenu() {
        while (true) {
            System.out.println("\n=== ADMIN MENU ===");
            System.out.println("1. View All Accounts");
            System.out.println("2. View Bank Total Assets");
            System.out.println("3. Apply Monthly Interest");
            System.out.println("4. Close Account");
            System.out.println("5. Back to Main Menu");
            System.out.print("Enter your choice: ");
            
            int choice = scanner.nextInt();
            scanner.nextLine();
            
            try {
                switch (choice) {
                    case 1:
                        viewAllAccounts();
                        break;
                    case 2:
                        viewBankAssets();
                        break;
                    case 3:
                        applyMonthlyInterest();
                        break;
                    case 4:
                        closeAccount();
                        break;
                    case 5:
                        isAdmin = false;
                        return;
                    default:
                        System.out.println("Invalid choice. Please try again.");
                }
            } catch (Exception ex) {
                System.out.println("Error: " + ex.getMessage());
            }
        }
    }

    private static void viewAllAccounts() {
        System.out.println("\n=== ALL ACCOUNTS ===");
        bank.getAllAccounts().forEach(account -> {
            System.out.printf("%-10s | %-20s | %-15s | $%,.2f%n",
                account.getAccountNumber(),
                account.getAccountHolder(),
                account.getClass().getSimpleName(),
                account.getBalance());
        });
    }

    private static void viewBankAssets() {
        System.out.printf("\nTotal Bank Assets: $%,.2f%n", bank.getTotalBankAssets());
    }

    private static void applyMonthlyInterest() {
        bank.applyMonthlyInterestToAllSavingsAccounts();
        System.out.println("Monthly interest applied to all savings accounts");
    }

    private static void closeAccount() throws AccountNotFoundException {
        System.out.print("Enter account number to close: ");
        String accountNumber = scanner.nextLine();
        bank.closeAccount(accountNumber);
        System.out.println("Account closed successfully");
    }

    private static void customerLogin() {
        System.out.println("\n1. Login with Account Number & PIN");
        System.out.println("2. Recover Account");
        System.out.print("Enter choice: ");
        int choice = scanner.nextInt();
        scanner.nextLine();
        
        try {
            if (choice == 1) {
                normalLogin();
            } else if (choice == 2) {
                recoverAccount();
            } else {
                System.out.println("Invalid choice");
            }
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }
    
    private static void normalLogin() throws AccountNotFoundException, InvalidPinException {
        System.out.print("\nEnter account number: ");
        String accountNumber = scanner.nextLine();
        
        System.out.print("Enter PIN: ");
        String pin = scanner.nextLine();
        
        currentAccount = bank.findAccount(accountNumber);
        if (!currentAccount.validatePin(pin)) {
            throw new InvalidPinException("Incorrect PIN");
        }
        
        showMainMenu();
    }
    
    private static void recoverAccount() throws AccountNotFoundException {
        System.out.print("\nEnter account number: ");
        String accountNumber = scanner.nextLine();
        
        System.out.print("Enter account holder full name: ");
        String fullName = scanner.nextLine();
        
        currentAccount = bank.recoverAccount(accountNumber, fullName);
        System.out.println("Account recovered successfully!");
        
        // Option to reset PIN
        System.out.print("Would you like to reset your PIN? (yes/no): ");
        String choice = scanner.nextLine();
        if (choice.equalsIgnoreCase("yes")) {
            resetPin();
        }
        
        showMainMenu();
    }
    
    private static void resetPin() {
        try {
            // First verify identity by asking for current PIN
            System.out.print("For security, please enter your current PIN: ");
            String currentPin = scanner.nextLine();
            
            if (!currentAccount.validatePin(currentPin)) {
                System.out.println("Incorrect PIN. PIN reset cancelled.");
                return;
            }
            
            // Get new PIN
            System.out.print("Create a new 4-digit PIN: ");
            String newPin = scanner.nextLine();
            
            if (newPin.length() != 4 || !newPin.matches("\\d+")) {
                System.out.println("PIN must be exactly 4 digits. Operation cancelled.");
                return;
            }
            
            // Change the PIN (using the current PIN we just validated)
            currentAccount.changePin(currentPin, newPin);
            System.out.println("PIN changed successfully!");
        } catch (InvalidPinException ex) {
            System.out.println("Error: " + ex.getMessage());
        }
    }


    private static void createAccount() {
        System.out.println("\nSelect Account Type:");
        System.out.println("1. Savings Account");
        System.out.println("2. Checking Account");
        System.out.print("Enter choice: ");
        int typeChoice = scanner.nextInt();
        scanner.nextLine();
        
        System.out.print("Enter account holder full name: ");
        String accountHolder = scanner.nextLine();
        
        
        String pin;
        while (true) {
            System.out.print("Create a 4-digit PIN: ");
            pin = scanner.nextLine();
            if (pin.length() == 4 && pin.matches("\\d+")) {
                break;
            }
            System.out.println("PIN must be exactly 4 digits. Please try again.");
        }
        
        System.out.print("Enter initial balance: ");
        double initialBalance = scanner.nextDouble();
        scanner.nextLine();
        
        try {
            switch (typeChoice) {
                case 1:
                    System.out.print("Enter interest rate (%): ");
                    double interestRate = scanner.nextDouble();
                    scanner.nextLine();
                    bank.createSavingsAccount(accountHolder, pin, initialBalance, interestRate);
                    break;
                case 2:
                    System.out.print("Enter overdraft limit: ");
                    double overdraftLimit = scanner.nextDouble();
                    scanner.nextLine();
                    bank.createCheckingAccount(accountHolder, pin, initialBalance, overdraftLimit);
                    break;
                default:
                    System.out.println("Invalid account type");
                    return;
            }
            
            // Display the account number to the user
            Account newAccount = bank.getAllAccounts().get(bank.getAllAccounts().size() - 1);
            System.out.println("\nAccount created successfully!");
            System.out.println("Your account number is: " + newAccount.getAccountNumber());
            System.out.println("Please save this number for future reference.");
            
        } catch (Exception e) {
            System.out.println("Error creating account: " + e.getMessage());
        }
    }
    
    private static void changePin() {
        try {
            System.out.print("Enter current PIN: ");
            String currentPin = scanner.nextLine();
            
            System.out.print("Enter new PIN: ");
            String newPin = scanner.nextLine();
            
            currentAccount.changePin(currentPin, newPin);
            System.out.println("PIN changed successfully!");
        } catch (InvalidPinException ex) {
            System.out.println("Error: " + ex.getMessage());
        }
    }
    

    private static void showMainMenu() {
        while (true) {
            System.out.println("\n=== MAIN MENU ===");
            System.out.printf("Account: %s (%s)%n", 
                currentAccount.getAccountNumber(), 
                currentAccount.getClass().getSimpleName());
            System.out.println("1. Deposit");
            System.out.println("2. Withdraw");
            System.out.println("3. Check Balance");
            System.out.println("4. View Transactions");
            System.out.println("5. Transfer Money");
            if (currentAccount instanceof SavingsAccount) {
                System.out.println("6. Apply Interest");
            }
            System.out.println("7. View Account Details");
            System.out.println("8. Change PIN");
            System.out.println("0. Logout");
            System.out.print("Enter your choice: ");
            
            int choice = scanner.nextInt();
            scanner.nextLine();
            
            try {
                switch (choice) {
                    case 1:
                        handleDeposit();
                        break;
                    case 2:
                        handleWithdrawal();
                        break;
                    case 3:
                        currentAccount.displayBalance();
                        break;
                    case 4:
                        currentAccount.displayTransactions();
                        break;
                    case 5:
                        handleTransfer();
                        break;
                    case 6:
                        if (currentAccount instanceof SavingsAccount) {
                            ((SavingsAccount)currentAccount).applyInterest();
                            System.out.println("Interest applied successfully!");
                        }
                        break;
                    case 7:
                        displayAccountDetails();
                        break;
                    case 8:
                        changePin();
                        break;
                    case 0:
                    	saveBankData();
                        currentAccount = null;
                        return;
                    default:
                        System.out.println("Invalid choice. Please try again.");
                }
            } catch (InsufficientFundsException | InvalidAmountException | AccountNotFoundException ex) {
                System.out.println("Error: " + ex.getMessage());
            }
        }
    }

    private static void displayAccountDetails() {
        System.out.println("\n=== ACCOUNT DETAILS ===");
        System.out.println("Account Number: " + currentAccount.getAccountNumber());
        System.out.println("Account Holder: " + currentAccount.getAccountHolder());
        System.out.println("Account Type: " + currentAccount.getClass().getSimpleName());
        
        if (currentAccount instanceof SavingsAccount) {
            System.out.printf("Interest Rate: %.2f%%%n", 
                ((SavingsAccount)currentAccount).getInterestRate());
        } else if (currentAccount instanceof CheckingAccount) {
            System.out.printf("Overdraft Limit: $%.2f%n", 
                ((CheckingAccount)currentAccount).getOverdraftLimit());
        }
        
        currentAccount.displayBalance();
    }

    private static void handleDeposit() throws InvalidAmountException {
        System.out.print("Enter deposit amount: ");
        double amount = scanner.nextDouble();
        scanner.nextLine();
        currentAccount.deposit(amount);
        System.out.printf("$%,.2f deposited successfully.%n", amount);
    }

    private static void handleWithdrawal() throws InsufficientFundsException, InvalidAmountException {
        System.out.print("Enter withdrawal amount: ");
        double amount = scanner.nextDouble();
        scanner.nextLine();
        currentAccount.withdraw(amount);
        System.out.printf("$%,.2f withdrawn successfully.%n", amount);
    }

    private static void handleTransfer() throws InsufficientFundsException, InvalidAmountException, AccountNotFoundException {
        System.out.print("Enter recipient account number: ");
        String recipientNumber = scanner.nextLine();
        Account recipient = bank.findAccount(recipientNumber);
        
        System.out.print("Enter transfer amount: ");
        double amount = scanner.nextDouble();
        scanner.nextLine();
        
        currentAccount.transfer(recipient, amount);
        System.out.printf("$%,.2f transferred to account %s successfully.%n", 
                         amount, recipientNumber);
    }
}