package org.Webbee.services;
import org.Webbee.exceptions.DirectoryProcessingException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Stream;

/**
 * Класс для рекурсивного чтения лог-файлов из указанной директории.
 * Реализует AutoCloseable для автоматического освобождения ресурсов.
 */
public class DirectoryReader implements AutoCloseable {
    private final Path path;
    private Stream<Path> fileStream;

    /**
     * Создает новый экземпляр для чтения файлов из указанной директории.
     *
     * @param path путь к директории с лог-файлами
     * @throws DirectoryProcessingException если путь не существует или не является директорией
     * @throws IllegalArgumentException если path равен null
     */
    public DirectoryReader(String path ) throws DirectoryProcessingException {
        if (path == null) {
            throw new IllegalArgumentException("Path cannot be null");
        }
        this.path = Paths.get(path).toAbsolutePath();
        if (!Files.isDirectory(this.path)) {
            throw new DirectoryProcessingException("Error: " + this.path + " is not a directory");
        }
    }

    /**
     * Возвращает Stream<Path> всех .log файлов в директории и поддиректориях.
     * <p>
     * Каждый вызов метода закрывает предыдущий поток (если был открыт).
     *
     * @return поток путей к файлам с расширением .log
     * @throws IOException при ошибках чтения файловой системы
     */
    public Stream<Path> getFileStream() throws IOException {
        if (fileStream != null) {
            fileStream.close();
        }
        fileStream = Files.walk(this.path)
                .filter(Files::isRegularFile)
                .filter(p -> p.toString().endsWith(".log"));
        return fileStream;
    }

    /**
     * Закрывает ресурсы, связанные с потоком файлов.
     * <p>
     * Метод вызывается автоматически при использовании try-with-resources.
     * Повторные вызовы метода безопасны.
     */
    @Override
    public void close() {
        if (fileStream != null) {
            fileStream.close();
        }
    }

}