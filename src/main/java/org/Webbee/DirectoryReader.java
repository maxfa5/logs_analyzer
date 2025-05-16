package org.Webbee;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Stream;

public class DirectoryReader implements AutoCloseable {
    private final Path path;
    private Stream<Path> fileStream;

    DirectoryReader(String path ) throws RuntimeException {
        this.path = Paths.get(path).toAbsolutePath();
        if (!Files.isDirectory(this.path)) {
            throw  new RuntimeException("Error: " + this.path + " is not a directory");
        }
    }

    public Stream<Path> getFileStream() throws IOException {
        if (fileStream != null) {
            fileStream.close();
        }
        fileStream = Files.walk(this.path)
                .filter(Files::isRegularFile)
                .filter(p -> p.toString().endsWith(".log"));
        return fileStream;
    }
    @Override
    public void close() {
        if (fileStream != null) {
            fileStream.close();
        }
    }

}