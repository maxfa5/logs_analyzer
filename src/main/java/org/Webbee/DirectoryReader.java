package org.Webbee;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Stream;

public class DirectoryReader {
    private final Path path;
    DirectoryReader(String path ) throws RuntimeException {
        Path directory = Paths.get(path).toAbsolutePath();
        if (!Files.isDirectory(directory)) {
            throw  new RuntimeException("Error: " + directory + " is not a directory");
        }
        this.path = Paths.get(path);
    }

    public Stream<Path> getFileStream() throws IOException {
        return Files.walk(this.path)
                .filter(Files::isRegularFile)
                .filter(p -> p.toString().endsWith(".log"));
    }
}