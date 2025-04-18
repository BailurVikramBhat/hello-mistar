package com.vikrambhat.core;

import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class AppLogger {
    public static final String LOG_FILE = "app.log";

    public void log(String message) {
        try (FileWriter writer = new FileWriter(LOG_FILE, true)) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy - HH:mm:ss");
            String timestamp = LocalDateTime.now().format(formatter);
            writer.write(timestamp + " - " + message + "\n");

        } catch (IOException e) {
            System.err.println("Failed to write log: " + e.getMessage());
        }
    }
}
