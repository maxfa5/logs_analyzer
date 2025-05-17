package org.Webbee.services;

import org.Webbee.model.Transaction;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LineParser {
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private static final Pattern LOG_PATTERN = Pattern.compile(
            "^\\[(.+?)] (.+?) (balance inquiry|transferred|withdrew) (\\d+\\.?\\d*) ?(?:to (.+))?$"
    );

    public static Transaction parseLine(String line) {
        Matcher matcher = LOG_PATTERN.matcher(line);
        if (!matcher.find()) {
            return null;
        }

        LocalDateTime timestamp = LocalDateTime.parse(matcher.group(1), DATE_FORMATTER);
        String userId = matcher.group(2);
        String operation = matcher.group(3);
        BigDecimal amount = BigDecimal.valueOf(Double.parseDouble(matcher.group(4)));

        if ("balance inquiry".equals(operation)) {
            return new Transaction(Transaction.OperationType.BALANCE_INQUIRY, timestamp, userId, amount);
        } else if ("transferred".equals(operation)) {
            return new Transaction(Transaction.OperationType.TRANSFERRED, timestamp, userId, amount, matcher.group(5));
        } else if ("withdrew".equals(operation)) {
            return new Transaction(Transaction.OperationType.WITHDREW, timestamp, userId, amount);
        } else {
            return null;
        }
    }


}
