package org.webbee.services;

import org.webbee.exceptions.InitializationException;
import org.webbee.model.Transaction;
import org.webbee.model.User;

import java.io.BufferedWriter;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.Set;

public class LogWriter {
    private static Path  outputDirectory;
    private static final SimpleDateFormat TIMESTAMP_FORMATTER =
            new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private LogWriter() {}

    /**
     * Инициализирует файл логов
     * @param destinationPath путь к файлу логов
     * @param PathOfDir путь к директории в которой хранятся логи
     * @throws InitializationException если путь пустой/null или если не удалось создать файл
     */
    public static void initialize(String destinationPath, String PathOfDir) throws InitializationException {
        if (destinationPath == null || destinationPath.trim().isEmpty()) {
            throw new InitializationException("Destination path cannot be null or empty");
        }
        if (PathOfDir == null || PathOfDir.trim().isEmpty()) {
            throw new InitializationException("Path of directory cannot be null or empty");
        }

        Path directoryPath = Paths.get(PathOfDir).toAbsolutePath();
        outputDirectory = directoryPath.resolve(destinationPath).toAbsolutePath();
        try {
            if (Files.notExists(outputDirectory)) {
                Files.createDirectories(outputDirectory);
            }
        } catch (IOException e) {
            throw new InitializationException("Failed to initialize log file at " + outputDirectory);
        }
    }

    /**
     * Записывает логи всех пользователей
     * @param users Map пользователей для записи
     * @throws RuntimeException если произошла ошибка записи
     */
    public static void writeUsers(Map<String, User> users){
        users.forEach((key, value) -> {
            try {
                LogWriter.writeUserLogs((key + ".log"), value.getTransactionLogs(), value.getBalance());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
    }
    /**
     * @param filename имя файла для записи
     * @param logs набор транзакций
     * Записывает логи пользователя в файл
     * @param finalBalance итоговый баланс
     * @throws IOException если произошла ошибка записи
     * @throws IllegalStateException если директория не инициализирована
     */
    public static void writeUserLogs(String filename, Set<Transaction> logs, BigDecimal finalBalance) throws IOException {
        Path outputPath = outputDirectory.resolve(filename);
        String userName = filename.substring(0, filename.lastIndexOf('.'));

        try (BufferedWriter writer = Files.newBufferedWriter(outputPath)) {
            // Запись всех транзакций
            for (Transaction log : logs) {
                writer.write(log.toString());
                writer.newLine();
            }

            // Запись итогового баланса
            String timestamp = TIMESTAMP_FORMATTER.format(new Date());
            String balanceEntry = String.format("[%s] %s final balance %.2f",
                    timestamp, userName, finalBalance);
            writer.write(balanceEntry);
            writer.newLine();
        }

    }

}
