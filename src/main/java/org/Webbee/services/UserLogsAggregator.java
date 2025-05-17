package org.Webbee.services;

import org.Webbee.model.Transaction;
import org.Webbee.model.User;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Stream;

public class UserLogsAggregator {
    private final Map<String, User> users;

    public UserLogsAggregator(Stream<Path> paths) {
        this.users = new HashMap<String, User>();
        aggregateFromFileStream(paths);
    }

    private void aggregateFromFileStream(Stream<Path> paths){
        paths.forEach(path-> {
            try {
                processFile(path);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });

    }


    private void processFile(Path src)throws IOException {
        List<String> lines = Files.readAllLines(src);
        lines.stream()
            .map(LineParser::parseLine)
            .filter(Objects::nonNull)
            .forEach(this::processTransaction);
    }

    private void processTransaction(Transaction transaction) {
        Objects.requireNonNull(transaction, "Transaction cannot be null");

        String senderId = transaction.getSender();
        users.putIfAbsent(senderId, new User(senderId));
        User sender = users.get(senderId);

        switch (transaction.getOperationType()) {
            case WITHDREW:
                handleWithdrawal(sender, transaction);
                break;
            case BALANCE_INQUIRY:
                handleBalanceInquiry(sender, transaction);
                break;
            case TRANSFERRED:
                handleTransfer(transaction, sender);
                break;
            default:
                throw new IllegalArgumentException("Unknown operation type: " +
                        transaction.getOperationType());
        }
    }

    private void handleWithdrawal(User user, Transaction transaction) {
        try {
            user.withdraw(transaction.getAmount());
            user.addTransaction(transaction);
        } catch (IllegalStateException e) {
            System.err.println("Withdrawal failed for user " + user.getName() +
                    ": " + e.getMessage());
        }
    }

    private void handleBalanceInquiry(User user, Transaction transaction) {
        user.balanceInquiry(transaction.getAmount());
        user.addTransaction(transaction);
    }

    private void handleTransfer(Transaction transaction, User sender) {
        String recipientId = transaction.getRecipient();

        users.putIfAbsent(recipientId, new User(recipientId));
        User recipient = users.get(recipientId);

        try {
            sender.transfer(transaction.getAmount(), recipient);
            sender.addTransaction(transaction);
        } catch (IllegalStateException e) {
            System.err.println("Transfer failed from " + sender.getName() +
                    " to " + recipient.getName() + ": " + e.getMessage());
        }
    }
    public Map<String, User> getUsers() {
        return Collections.unmodifiableMap(users);
    }
}
