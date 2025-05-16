package org.Webbee.model;

import java.math.BigDecimal;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

public class User {
    private final String name;
    private BigDecimal balance;
    private final SortedSet<Transaction> transactions = new TreeSet<>();

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
        return new TreeSet<>(transactions); // Возвращаем копию для безопасности
    }

    public void balanceInquiry(BigDecimal amount) {
        balance = amount;
    }

    public void transfer(BigDecimal amount, User recipient) {
        this.balance = this.balance.subtract(amount);
        recipient.balance = recipient.balance.add(amount);
    }

    public void withdraw(BigDecimal amount) {
        balance = balance.subtract(amount);
    }

    public void addTransaction(Transaction transaction) {
        transactions.add(transaction);
    }


}
