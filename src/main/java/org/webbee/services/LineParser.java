package org.webbee.services;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.webbee.model.Transaction;

/**
 * Парсер строк логов для преобразования в объекты Transaction.
 *
 * <p>Обрабатывает строки в формате: {@code [дата] пользователь операция сумма [получатель]}
 * Поддерживаемые операции: balance inquiry, transferred, withdrew
 */
public class LineParser {
  private static final DateTimeFormatter DATE_FORMATTER
      = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
  
  /**
   * Регулярное выражение для разбора строк логов.
   * Группы:
   * <ol>
   *   <li>Дата и время</li>
   *   <li>Идентификатор пользователя</li>
   *   <li>Тип операции</li>
   *   <li>Сумма</li>
   *   <li>Получатель (опционально)</li>
   * </ol>
   */
  private static final Pattern LOG_PATTERN = Pattern.compile(
      "^\\[(.+?)] (.+?) (balance inquiry|transferred|withdrew) (\\d+\\.?\\d*) ?(?:to (.+))?$"
  );
  
  /**
   * Парсит строку лога в объект Transaction.
   *
   * @param line строка лога для парсинга
   * @return объект Transaction или {@code null}, если строка не соответствует формату
   * @throws IllegalArgumentException если строка содержит некорректные данные
   */
  public static Transaction parseLine(String line) {
    Matcher matcher = LOG_PATTERN.matcher(line);
    if (!matcher.find()) {
      return null;
    }
    
    LocalDateTime timestamp = LocalDateTime.parse(matcher.group(1), DATE_FORMATTER);
    String userId = matcher.group(2);
    String operation = matcher.group(3);
    BigDecimal amount = new BigDecimal(matcher.group(4));
    
    if ("balance inquiry".equals(operation)) {
      return new Transaction(Transaction.OperationType.BALANCE_INQUIRY,
          timestamp, userId, amount);
    } else if ("transferred".equals(operation)) {
      return new Transaction(Transaction.OperationType.TRANSFERRED, timestamp,
          userId, amount, matcher.group(5));
    } else if ("withdrew".equals(operation)) {
      return new Transaction(Transaction.OperationType.WITHDREW, timestamp,
          userId, amount);
    } else {
      return null;
    }
  }
  
  
}
