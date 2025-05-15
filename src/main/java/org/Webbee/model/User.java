package org.Webbee.model;

import java.math.BigDecimal;
import java.util.Set;
import java.util.TreeSet;

public class User {
    private final String name;
    private BigDecimal balance;
    private final Set<Transaction> logs = new TreeSet<>();

    public User(String name) {
        this.name = name;
        balance = BigDecimal.ZERO;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public String getName() {
        return name;
    }

    public Set<Transaction> getTransactionLogs() {
        return new TreeSet<>(logs); // Возвращаем копию для безопасности
    }

    public void balanceInquiry(BigDecimal amount) {
        balance = amount;
//        logs.add(new Transaction(Transaction.OperationType.BALANCE_INQUIRY, amount));
    }

    public void transfer(BigDecimal amount, User recipient) {
        if (this == recipient) {
            throw new IllegalArgumentException("Cannot transfer to yourself");
        }
        this.balance = this.balance.subtract(amount);
        recipient.balance = recipient.balance.add(amount);

//        this.logs.add(new Transaction(Transaction.OperationType.TRANSFERRED, amount, recipient.getName()));
    }

    public void withdraw(BigDecimal amount) {
        balance = balance.subtract(amount);
//        logs.add(new Transaction(Transaction.OperationType.WITHDREW, amount));
    }

    public void addTransaction(Transaction transaction) {
        logs.add(transaction);
    }


//
//    private void validateAmount(BigDecimal amount) {
//        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
//            throw new IllegalArgumentException("Amount must be positive");
//        }
//    }
}
