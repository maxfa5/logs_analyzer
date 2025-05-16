package org.Webbee;

import static java.lang.System.exit;


public class Main {
    private static final String TRANSACTIONS_DIR_NAME = "transactions_by_users";
    private static final int ERROR_EXIT_CODE = 1;

    public static void main(String[] args) {
        validateArguments(args);
        try {
            LogWriter.initialize(TRANSACTIONS_DIR_NAME);
            UserLogsAggregator aggregator = new UserLogsAggregator();
            try (DirectoryReader reader = new DirectoryReader(args[0])){
                aggregator.agregateFromFileStream(reader.getFileStream());
            } catch (Exception e) {
                System.err.println("Error processing directory: " + e.getMessage());
                System.exit(ERROR_EXIT_CODE);
            }
            LogWriter.writeUsers(aggregator.getUsers());
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
            System.exit(ERROR_EXIT_CODE);
        }
    }

    private static void validateArguments(String[] args) {
        if (args.length < 1) {
            System.out.println("Отсутствует путь к директории.");
            exit(ERROR_EXIT_CODE);
        }
    }
}