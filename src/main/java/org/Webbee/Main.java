package org.Webbee;

import static java.lang.System.exit;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.Files;
//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {
    public static void main(String[] args) {
        if (args.length < 1) {
            System.out.println("Отсутствует путь к директории.");
            exit(1);
        }

        DirectoryReader reader = new DirectoryReader(args[0]);
        LogWriter.initializeLogFile("transactions_by_users");
        UserLogsAggregator agreagtor = new UserLogsAggregator();
        try {
            agreagtor.agregateFromFileStream(reader.getFileStream());
        } catch (Exception e) {
            System.err.println("Error processing directory: " + e.getMessage());
            System.exit(3);
        }

        agreagtor.getUsers().forEach((key, value) -> {
            try {
                LogWriter.writeUserLogs((key.toString() + ".log"), value.getTransactionLogs(), value.getBalance());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });



    }
}