package org.webbee.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

/**
 * Класс, представляющий финансовую транзакцию.
 * Реализует Comparable для сортировки транзакций по времени.
 */
public class Transaction implements Comparable<Transaction> {
  /**
   * Типы поддерживаемых операций.
   */
  public enum OperationType {
    BALANCE_INQUIRY,
    TRANSFERRED,
    WITHDREW
  }
  
  private final String sender;
  private final OperationType operationType;
  private final BigDecimal amount;
  private final String recipient;
  private final LocalDateTime timestamp;
  private static final DateTimeFormatter formatter =
      DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
  
  /**
   * Конструктор для операций BALANCE_INQUIRY и WITHDREW.
   *
   * @param operationType тип операции
   * @param timestamp     временная метка операции
   * @param sender        отправитель/инициатор операции
   * @param amount        сумма операции
   * @throws IllegalArgumentException если сумма неположительная
   */
  public Transaction(OperationType operationType,
                     LocalDateTime timestamp,
                     String sender,
                     BigDecimal amount) throws IllegalArgumentException {
    this(operationType, timestamp, sender, amount, null);
  }
  
  /**
   * Конструктор для операций TRANSFERRED.
   *
   * @param operationType тип операции
   * @param timestamp     временная метка операции
   * @param sender        отправитель/инициатор операции
   * @param amount        сумма перевода
   * @param recipient     получатель перевода
   * @throws IllegalArgumentException если сумма неположительная или отсутствует получатель
   */
  public Transaction(OperationType operationType,
                     LocalDateTime timestamp,
                     String sender,
                     BigDecimal amount,
                     String recipient) throws IllegalArgumentException {
    if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
      throw new IllegalArgumentException("Amount must be positive");
    }
    
    if (operationType == OperationType.TRANSFERRED
        && (recipient == null || recipient.trim().isEmpty())) {
      throw new IllegalArgumentException("Recipient is required for TRANSFERRED operation");
    }
    
    this.sender = sender;
    this.operationType = operationType;
    this.amount = amount;
    this.recipient = operationType == OperationType.TRANSFERRED ? recipient : null;
    this.timestamp = timestamp;
  }
  
  
  public OperationType getOperationType() {
    return operationType;
  }
  
  public BigDecimal getAmount() {
    return amount;
  }
  
  public String getRecipient() {
    return recipient;
  }
  
  public LocalDateTime getTimestamp() {
    return timestamp;
  }
  
  public String getSender() {
    return sender;
  }
  
  /**
   * Форматированное строковое представление транзакции.
   * Формат: "[время] отправитель операция сумма получатель"
   */
  @Override
  public String toString() {
    String formattedTime = timestamp.format(formatter);
    switch (operationType) {
      case BALANCE_INQUIRY:
        return String.format("[%s] %s balance inquiry %s",
            formattedTime, this.sender,
            amount.stripTrailingZeros().toPlainString());
      case TRANSFERRED:
        return String.format("[%s] %s transferred %s to %s",
            formattedTime, this.sender,
            amount.stripTrailingZeros().toPlainString(), recipient);
      case WITHDREW:
        return String.format("[%s] %s withdrew %s",
            formattedTime, this.sender,
            amount.stripTrailingZeros().toPlainString());
      default:
        throw new IllegalStateException("Unknown operation type");
    }
  }
  
  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    Transaction that = (Transaction) o;
    return operationType == that.operationType
        && amount.compareTo(that.amount) == 0
        && Objects.equals(recipient, that.recipient)
        && Objects.equals(timestamp, that.timestamp)
        && Objects.equals(sender, that.sender);
  }
  
  @Override
  public int hashCode() {
    return Objects.hash(operationType, amount, recipient, timestamp, sender);
  }
  
  /**
   * Сравнение транзакций по времени (для сортировки).
   */
  @Override
  public int compareTo(Transaction other) {
    return this.timestamp.compareTo(other.timestamp);
  }
}
