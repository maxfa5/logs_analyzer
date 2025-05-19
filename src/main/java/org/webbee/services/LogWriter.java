package org.webbee.services;

import java.io.BufferedWriter;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.webbee.exceptions.InitializationException;
import org.webbee.model.Transaction;
import org.webbee.model.User;

/**
 * Класс для записи логов транзакций пользователей.
 * Обеспечивает инициализацию директории логов и запись данных.
 */
public class LogWriter {
  private static Path outputDirectory;
  private static final DateTimeFormatter TIMESTAMP_FORMATTER =
      DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
  
  private LogWriter() {
  }
  
  /**
   * Инициализирует файл логов.
   *
   * @param destinationPath путь к файлу логов
   * @param pathOfDir       путь к директории в которой хранятся логи
   * @throws InitializationException если путь пустой/null или если не удалось создать файл
   */
  public static void initialize(String destinationPath, String pathOfDir)
      throws InitializationException {
    if (destinationPath == null || destinationPath.trim().isEmpty()) {
      throw new InitializationException("Destination path cannot be null or empty");
    }
    if (pathOfDir == null || pathOfDir.trim().isEmpty()) {
      throw new InitializationException("Path of directory cannot be null or empty");
    }
    
    Path directoryPath = Paths.get(pathOfDir).toAbsolutePath();
    outputDirectory = directoryPath.resolve(destinationPath).toAbsolutePath();
    try {
      if (!Files.notExists(outputDirectory.getParent()) && Files.notExists(outputDirectory)) {
        Files.createDirectories(outputDirectory);
      }
    } catch (IOException e) {
      throw new InitializationException("Failed to initialize log file at " + outputDirectory);
    }
  }
  
  /**
   * Асинхронно записывает логи всех пользователей.
   *
   * <p>Использует пул потоков для параллельной записи файлов.
   *
   * <p>Каждый Future асинхронно обрабатывает запись логов для одного пользователя из Map.
   *
   * @param users Map пользователей для записи
   * @throws RuntimeException если произошла ошибка записи
   */
  
  public static void writeUsers(Map<String, User> users) {
    ExecutorService executor = Executors.newFixedThreadPool(Runtime.getRuntime()
        .availableProcessors());
    try {
      CompletableFuture<?>[] futures = users.entrySet().parallelStream()
          .map(entry -> CompletableFuture.runAsync(() -> {
            try {
              String filename = entry.getKey() + ".log";
              LogWriter.writeUserLogs(filename, entry.getValue().getTransactionLogs(),
                  entry.getValue().getBalance());
            } catch (IOException e) {
              throw new RuntimeException("Failed to write logs for user " + entry.getKey(), e);
            }
          }, executor))
          .toArray(CompletableFuture[]::new);
      
      CompletableFuture.allOf(futures).join();
    } finally {
      executor.shutdown();
    }
  }
  
  /**
   * Запись логов для 1 пользователя.
   *
   * @param filename     имя файла для записи
   * @param logs         набор транзакций
   *                     Записывает логи пользователя в файл
   * @param finalBalance итоговый баланс
   * @throws IOException           если произошла ошибка записи
   * @throws IllegalStateException если директория не инициализирована
   */
  public static void writeUserLogs(String filename, Set<Transaction> logs, BigDecimal finalBalance)
      throws IOException {
    Path outputPath = outputDirectory.resolve(filename);
    String userName = filename.substring(0, filename.lastIndexOf('.'));
    
    try (BufferedWriter writer = Files.newBufferedWriter(outputPath)) {
      // Запись всех транзакций
      for (Transaction log : logs) {
        writer.write(log.toString());
        writer.newLine();
      }
      
      // Запись итогового баланса
      String timestamp = LocalDateTime.now().format(TIMESTAMP_FORMATTER);
      String balanceEntry = String.format("[%s] %s final balance %f",
          timestamp, userName, finalBalance);
      writer.write(balanceEntry);
      writer.newLine();
    }
    
  }
  
}
