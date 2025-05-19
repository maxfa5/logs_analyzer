package org.webbee;

import static java.lang.System.exit;

import org.webbee.exceptions.DirectoryProcessingException;
import org.webbee.exceptions.InitializationException;
import org.webbee.services.DirectoryReader;
import org.webbee.services.LogWriter;
import org.webbee.services.UserLogsAggregator;


public class Main {
  private static final String TRANSACTIONS_DIR_NAME = "transactions_by_users";
  private static final int ERROR_EXIT_CODE = 1;
  
  public static void main(String[] args) {
    validateArguments(args);
    try {
      initializeLogWriter(args[0]);
    } catch (InitializationException e) {
      System.err.println("Initialization error: " + e.getMessage());
      System.exit(ERROR_EXIT_CODE);
    }
    
    try (DirectoryReader reader = new DirectoryReader(args[0])) {
      UserLogsAggregator aggregator = new UserLogsAggregator(reader.getFileStream());
      LogWriter.writeUsers(aggregator.getUsers());
    } catch (DirectoryProcessingException e) {
      System.err.println("Error processing directory: " + e.getMessage());
      System.exit(ERROR_EXIT_CODE);
    } catch (Exception e) {
      System.err.println("Error: " + e.getMessage());
      System.exit(ERROR_EXIT_CODE);
    }
    System.out.println("Logs processed successfully");
    
  }
  
  private static void initializeLogWriter(String path) throws InitializationException {
    try {
      LogWriter.initialize(TRANSACTIONS_DIR_NAME, path);
    } catch (Exception e) {
      throw new InitializationException("Failed to initialize LogWriter");
    }
  }
  
  private static void validateArguments(String[] args) {
    if (args.length < 1) {
      System.out.println("Отсутствует путь к директории.");
      exit(ERROR_EXIT_CODE);
    }
  }
}