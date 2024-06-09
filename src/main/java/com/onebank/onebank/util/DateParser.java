package com.onebank.onebank.util;

import javax.swing.text.DateFormatter;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;

public class DateParser {

    public static LocalDateTime parseDate(String dateString) {
        String[] patterns = {"dd-MM-yyyy", "yyyy-MM-dd'T'HH:mm:ss", "yyyy-MM-dd", "yyyy-MM-dd HH:mm:ss.SSSSSS"};
        for (String pattern : patterns) {
            try {
                DateFormatter formatter = new DateFormatter();
                if (pattern.equals("dd-MM-yyyy") || pattern.equals("yyyy-MM-dd")) {
                    return LocalDate.parse(dateString).atStartOfDay().plusHours(10);
                } else {
                    return LocalDateTime.parse(dateString);
                }
            } catch (DateTimeParseException e) {
                // Continue to the next pattern
            }
        }
        throw new IllegalArgumentException("Invalid date format: " + dateString);
    }
}