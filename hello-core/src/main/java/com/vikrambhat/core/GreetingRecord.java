package com.vikrambhat.core;

import java.util.Arrays;

public class GreetingRecord {
    private static final AppLogger logger = new AppLogger();
    private final String language;
    private final String name;
    private final String greeting;
    private final String timestamp;

    public GreetingRecord(String language, String name, String greeting, String timestamp) {
        this.language = language;
        this.name = name;
        this.greeting = greeting;
        this.timestamp = timestamp;
        logger.log("GreetingRecord:GreetingRecord() - " + language + ", " + name + ", " + greeting + ", " + timestamp);
    }

    public String toJson() {
        return "{\n" +
                "  \"language\": \"" + language + "\",\n" +
                "  \"name\": \"" + name + "\",\n" +
                "  \"greeting\": \"" + greeting + "\",\n" +
                "  \"timestamp\": \"" + timestamp + "\"\n" +
                "}";
    }

    public static GreetingRecord fromLogLine(String line) {
        logger.log("GreetingRecord::fromLogLine - started");
        String[] parts = line.split("\\|");
        if (parts.length < 4) {
            return new GreetingRecord("unknown", "unknown", "invalid log line", "unknown");
        }
        logger.log("GreetingRecord::fromLogLine - parts: " + Arrays.toString(parts));
        return new GreetingRecord(parts[0], parts[1], parts[2], parts[3]);
    }
}
