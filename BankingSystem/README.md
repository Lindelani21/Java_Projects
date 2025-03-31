# Bank Account Management System


## Features
### Account Management
- Create Savings/Checking accounts
- Deposit/withdraw funds
- Transfer between accounts
- View transaction history

### Security
- 4-digit PIN authentication
- Account recovery system
- Encrypted data persistence

### Reporting
- Balance inquiries
- Bank-wide asset tracking
- Interest calculations

## OOP Principles Implemented

| Principle        | Implementation Example                          |
|------------------|-----------------------------------------------|
| Abstraction      | `Account` abstract class with core operations |
| Encapsulation    | Private fields with public getters/setters    |
| Inheritance      | `SavingsAccount` and `CheckingAccount` child classes |
| Polymorphism     | Overridden `withdraw()` methods per account type |

## Data Structures Used
- **Collections Framework**
  - `ArrayList<Transaction>` for transaction history
  - `List<Account>` for bank account management
- **Exception Handling**
  - Custom exceptions (`InsufficientFundsException`, `InvalidPinException`)
- **File I/O**
  - Object serialization for data persistence
  - `java.io` package for file operations
 
## Best Practices Demonstrated
### SOLID Principles
- Single Responsibility (separate classes for bank/account/transaction)
- Open/Closed (extendable via new account types)
- Liskov Substitution (child classes maintain parent contracts)
### Design Patterns
- Factory Pattern (account creation)
- Singleton Pattern (Bank instance)
### Security
- PIN validation without exposure
- Encapsulated data

## System Architecture

```mermaid
classDiagram
    class Account {
        <<abstract>>
        -String accountNumber
        -String accountHolder
        -double balance
        +deposit()
        +withdraw()
        +transfer()
    }
    
    class SavingsAccount {
        -double interestRate
        +applyInterest()
    }
    
    class CheckingAccount {
        -double overdraftLimit
    }
    
    class Bank {
        -List~Account~ accounts
        +createAccount()
        +findAccount()
    }
    
    Account <|-- SavingsAccount
    Account <|-- CheckingAccount
    Bank "1" *-- "*" Account





