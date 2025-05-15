package org.Webbee;

import org.Webbee.model.Transaction;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.Set;

public class LogWriter {
    private static Path  destination;
    private static final SimpleDateFormat TIMESTAMP_FORMATTER =
            new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private LogWriter() {}
    /**
     * Инициализирует файл логов
     * @param destinationPath путь к файлу логов
     * @throws RuntimeException если не удалось создать файл
     * @throws IllegalArgumentException если путь пустой или null
     */
    public static void initializeLogFile(String destinationPath) {
        if (destinationPath == null || destinationPath.isBlank()) {
            throw new IllegalArgumentException("Destination path cannot be null or empty");
        }

        destination = Paths.get(destinationPath).toAbsolutePath();

        try {
            if (Files.notExists(destination)) {
                Files.createDirectories(destination);
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to initialize log file at " + destination, e);
        }
    }

    public static void writeUserLogs(String outputPathStr, Set<Transaction> logs, BigDecimal finalBalance) throws IOException {
        Path outputPath = destination.resolve(outputPathStr);

        try (BufferedWriter writer = Files.newBufferedWriter(outputPath)) {
            // Запись всех транзакций
            for (Transaction log : logs) {
                writer.write(log.toString());
                writer.newLine();
            }
            String userName = outputPathStr.substring(0, outputPathStr.lastIndexOf('.'));

            // Запись итогового баланса
            String timestamp = TIMESTAMP_FORMATTER.format(new Date());
            String balanceEntry = String.format("[%s] %s final balance %.2f",
                    timestamp, userName, finalBalance);
            writer.write(balanceEntry);
            writer.newLine();
        }

    }

}
