package org.webbee.services;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Stream;
import org.webbee.model.Transaction;
import org.webbee.model.User;

/**
 * Класс для агрегации и обработки логов транзакций пользователей.
 *
 * <p>Собирает данные из файлов логов, группирует их по пользователям
 * и вычисляет итоговые балансы. Поддерживает операции:
 */
public class UserLogsAggregator {
  private final Map<String, User> users;
  
  /**
   * Создает агрегатор и сразу обрабатывает переданные файлы.
   *
   * @param paths поток путей к файлам с логами транзакций
   * @throws RuntimeException если произошла ошибка чтения файлов
   */
  
  public UserLogsAggregator(Stream<Path> paths) {
    this.users = new HashMap<String, User>();
    aggregateFromFileStream(paths);
  }
  
  /**
   * Обрабатывает поток файлов, извлекая транзакции.
   *
   * @param paths поток путей к файлам для обработки
   */
  
  private void aggregateFromFileStream(Stream<Path> paths) {
    paths.forEach(path -> {
      try {
        processFile(path);
      } catch (IOException e) {
        throw new RuntimeException(e);
      }
    });
    
  }
  
  /**
   * Обрабатывает отдельный файл с логами.
   *
   * @param src путь к файлу для обработки
   * @throws IOException если произошла ошибка чтения файла
   */
  private void processFile(Path src) throws IOException {
    Stream<String> lines = Files.lines(src);
    lines.map(LineParser::parseLine)
        .filter(Objects::nonNull)
        .forEach(this::processTransaction);
  }
  
  /**
   * Обрабатывает отдельную транзакцию.
   *
   * @param transaction транзакция для обработки
   * @throws IllegalArgumentException если тип операции неизвестен
   * @throws NullPointerException     если транзакция равна null
   */
  
  private void processTransaction(Transaction transaction)
      throws IllegalArgumentException, NullPointerException {
    Objects.requireNonNull(transaction, "Transaction cannot be null");
    
    String senderId = transaction.getSender();
    users.putIfAbsent(senderId, new User(senderId));
    User sender = users.get(senderId);
    
    switch (transaction.getOperationType()) {
      case WITHDREW:
        handleWithdrawal(sender, transaction);
        break;
      case BALANCE_INQUIRY:
        handleBalanceInquiry(sender, transaction);
        break;
      case TRANSFERRED:
        handleTransfer(transaction, sender);
        break;
      default:
        throw new IllegalArgumentException("Unknown operation type: "
            + transaction.getOperationType());
    }
  }
  
  /**
   * Обрабатывает операцию снятия средств.
   *
   * @param user        пользователь, совершающий операцию
   * @param transaction данные транзакции
   */
  
  private void handleWithdrawal(User user, Transaction transaction) {
    try {
      user.withdraw(transaction.getAmount());
      user.addTransaction(transaction);
    } catch (IllegalStateException e) {
      System.err.println("Withdrawal failed for user " + user.getName()
          + ": " + e.getMessage());
    }
  }
  
  /**
   * Обрабатывает операцию проверки баланса.
   *
   * @param user        пользователь, совершающий операцию
   * @param transaction данные транзакции
   */
  
  private void handleBalanceInquiry(User user, Transaction transaction) {
    user.balanceInquiry(transaction.getAmount());
    user.addTransaction(transaction);
  }
  
  /**
   * Обрабатывает операцию перевода средств.
   *
   * @param transaction данные транзакции
   * @param sender      пользователь-отправитель
   */
  private void handleTransfer(Transaction transaction, User sender) {
    String recipientId = transaction.getRecipient();
    
    users.putIfAbsent(recipientId, new User(recipientId));
    User recipient = users.get(recipientId);
    
    try {
      sender.transfer(transaction.getAmount(), recipient);
      sender.addTransaction(transaction);
    } catch (IllegalStateException e) {
      System.err.println("Transfer failed from " + sender.getName()
          + " to " + recipient.getName() + ": " + e.getMessage());
    }
  }
  
  /**
  * Возвращает неизменяемое отображение пользователей и их данных.
  *
  * @return неизменяемая Map, где ключ - имя пользователя,
  * значение - объект User с транзакциями и балансом
  */
  
  public Map<String, User> getUsers() {
    return Collections.unmodifiableMap(users);
  }
}
