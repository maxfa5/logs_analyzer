package org.Webbee.model;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

public class User {
    private final String name;
    private BigDecimal balance;
    private final SortedSet<Transaction> transactions = new TreeSet<>();

    /**
     * Создает нового пользователя с нулевым балансом.
     *
     * @param name имя пользователя (не может быть null или пустым)
     * @throws IllegalArgumentException если имя null или пустое
     */
    public User(String name) {
        if (name == null ||  name.trim().isEmpty()) {
            throw new IllegalArgumentException("Name cannot be null or empty");
        }
        this.name = name;
        balance = BigDecimal.ZERO;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public String getName() {
        return name;
    }

    /**
     * Возвращает неизменяемый набор транзакций пользователя.
     * Транзакции отсортированы по времени выполнения.
     *
     * @return неизменяемый набор транзакций
     */
    public SortedSet<Transaction> getTransactionLogs() {
        return Collections.unmodifiableSortedSet(transactions);
    }
    /**
     * Обновляет баланс пользователя.
     *
     * @param amount новое значение баланса
     * @throws IllegalArgumentException если amount null
     */
    public void balanceInquiry(BigDecimal amount) {
        if (amount == null) {
            throw new IllegalArgumentException("Amount cannot be null");
        }
        balance = amount;
    }

    /**
     * Выполняет перевод средств другому пользователю.
     *
     * @param amount сумма перевода (должна быть положительной)
     * @param recipient пользователь-получатель
     * @throws IllegalArgumentException если amount неположительный или recipient null
     */
    public void transfer(BigDecimal amount, User recipient) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Amount must be positive");
        }
        if (recipient == null) {
            throw new IllegalArgumentException("Recipient cannot be null");
        }
        this.balance = this.balance.subtract(amount);
        recipient.balance = recipient.balance.add(amount);
    }

    /**
     * Выполняет снятие средств с баланса пользователя.
     *
     * @param amount сумма снятия
     */
    public void withdraw(BigDecimal amount) {
        balance = balance.subtract(amount);
    }

    /**
     * Добавляет транзакцию в историю операций пользователя.
     *
     * @param transaction транзакция для добавления
     * @throws IllegalArgumentException если transaction null
     */
    public void addTransaction(Transaction transaction) {
        transactions.add(transaction);
    }


}
