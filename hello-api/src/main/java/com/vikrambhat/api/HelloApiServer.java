package com.vikrambhat.api;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.stream.Collectors;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import com.vikrambhat.core.AppLogger;
import com.vikrambhat.core.GreetingRecord;
import com.vikrambhat.core.HelloStrategy;
import com.vikrambhat.core.LanguageLoader;

public class HelloApiServer {
    private static final AppLogger logger = new AppLogger();

    private static final long START_TIME = System.currentTimeMillis();

    public static void start() throws IOException {
        logger.log("HelloApiServer:start - starting the server implementations");
        HttpServer server = HttpServer.create(new InetSocketAddress(8080), 0);
        server.createContext("/greet", new GreetHandler());
        server.createContext("/languages", new LanguageListHandler());
        server.createContext("/health", new HealthHandler());
        server.createContext("/history", new HistoryHandler());
        server.createContext("/metrics", new MetricsHandler());
        server.createContext("/docs", new SwaggerHandler());

        logger.log(
                "HelloApiServer:start - Successfully created context for /greet, ?languages, /health, /history and /metrics endpoints.");
        server.setExecutor(null);
        server.start();
        logger.log("HelloApiServer:start - server is started in the port 8080 of localhost.");
        System.out.println("API server started at http://localhost:8080");
    }

    static class GreetHandler implements HttpHandler {

        @Override
        public void handle(HttpExchange exchange) throws IOException {
            logger.log("GreetHandler:handle - Executing Handle for GreetHandler");
            if (!"GET".equals(exchange.getRequestMethod())) {
                exchange.sendResponseHeaders(405, -1);
                return;
            }
            Map<String, String> params = parseQuery(exchange.getRequestURI().getQuery());
            logger.log("GreetHandler:handle - parsed params: " + params);
            String name = params.getOrDefault("name", "world");
            String langClass = params.getOrDefault("lang", "EnglishHello");
            if (!langClass.contains(".")) {
                langClass = "com.vikrambhat.plugins." + langClass;
            }
            HelloStrategy strategy = LanguageLoader.loadLanguage(langClass);
            OutputStream os = exchange.getResponseBody();
            if (strategy == null) {
                String errorJson = "{ \"error\": \"Could not load language: " + langClass + "\" }";
                exchange.sendResponseHeaders(400, errorJson.getBytes().length);
                os.write(errorJson.getBytes());
                os.close();
                return;
            }
            logger.log("GreetHandler:handle - LangClass & strategy successfully loaded. ");
            String greetingText = (strategy != null) ? strategy.sayHello(name)
                    : "Could not load Language class: " + langClass;
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy - HH:mm:ss");
            String timestamp = LocalDateTime.now().format(formatter);
            try (FileWriter writer = new FileWriter("history.log", true)) {
                writer.write(langClass + "|" + name + "|" + greetingText + "|" + timestamp + "\n");
            } catch (IOException e) {
                System.err.println("Failed to write greeting to history: " + e.getMessage());
            }
            String json = "{\n" +
                    "  \"language\": \"" + langClass + "\",\n" +
                    "  \"name\": \"" + name + "\",\n" +
                    "  \"greeting\": \"" + greetingText + "\"\n" +
                    "}";
            exchange.getResponseHeaders().set("Content-Type", "application.json");
            exchange.sendResponseHeaders(200, json.getBytes().length);
            os.write(json.getBytes());
            os.close();
        }

        private Map<String, String> parseQuery(String query) {
            if (query == null)
                return Map.of();
            return Arrays.stream(query.split("&")).map(s -> s.split("=", 2))
                    .collect(Collectors.toMap(arr -> arr[0], arr -> arr.length > 1 ? arr[1] : ""));
        }

    }

    static class LanguageListHandler implements HttpHandler {

        @Override
        public void handle(HttpExchange exchange) throws IOException {

            File pluginsDirectory = new File(System.getProperty("user.dir"), "plugins");
            if (!pluginsDirectory.exists()) {
                String msg = "plugins directory does not exist.";
                logger.log("LanguageListHandler:handle - " + msg);
                exchange.sendResponseHeaders(200, msg.length());
                exchange.getResponseBody().write(msg.getBytes());
                exchange.getResponseBody().close();
                return;
            }
            logger.log("LanguageListHandler:handle - Successfully loaded plugins directory");
            File[] classFiles = pluginsDirectory.listFiles((dir, name) -> name.endsWith(".class"));
            logger.log("LanguageListhandler:handle - Successfully loaded plugin files. ");
            if (classFiles == null || classFiles.length == 0) {
                logger.log("LanguageListhandler:handle - No plugins found, throwing error.");
                String msg = "no plugins found.";
                exchange.sendResponseHeaders(200, msg.length());
                exchange.getResponseBody().write(msg.getBytes());
                exchange.close();
                return;
            }
            String list = Arrays.stream(classFiles).map(file -> file.getName().replace("Hello.class", ""))
                    .collect(Collectors.joining("\n"));
            logger.log("LanguageListhandler:handle - list of the languages: " + list);
            exchange.sendResponseHeaders(200, list.getBytes().length);
            exchange.getResponseBody().write(list.getBytes());
            exchange.close();
        }

    }

    static class HealthHandler implements HttpHandler {
        private static final String VERSION;
        private static final String BUILD_DATE;

        static {
            Properties props = new Properties();
            try (FileInputStream fis = new FileInputStream(System.getProperty("user.dir") + "\\build.properties")) {
                props.load(fis);
            } catch (IOException e) {
                System.err.println("Could not read build.properties: " + e.getMessage());
            }
            VERSION = props.getProperty("version", "unknown");
            BUILD_DATE = props.getProperty("build", "unknown");
        }

        @Override
        public void handle(HttpExchange exchange) throws IOException {
            File pluginsDirectory = new File("src/plugins");
            int pluginsCount = 0;
            if (pluginsDirectory.exists() && pluginsDirectory.isDirectory()) {
                File[] files = pluginsDirectory.listFiles((dir, name) -> name.endsWith(".class"));
                pluginsCount = files != null ? files.length : 0;
            }
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy - HH:mm:ss");
            String timestamp = LocalDateTime.now().format(formatter);
            List<String> memoryStats = getMemoryStats();

            String json = "{\n" +
                    "  \"status\": \"UP\",\n" +
                    "  \"plugins\": " + pluginsCount + ",\n" +
                    "  \"timestamp\": \"" + timestamp + "\",\n" +
                    "  \"uptime\": \"" + upTime() + "\",\n" +
                    "  \"memory\": {\n" +
                    "    \"used\": \"" + memoryStats.get(0) + "\",\n" +
                    "    \"free\": \"" + memoryStats.get(1) + "\",\n" +
                    "    \"total\": \"" + memoryStats.get(2) + "\"\n" +
                    "  },\n" +
                    "  \"version\": \"" + VERSION + "\",\n" +
                    "  \"build\": \"" + BUILD_DATE + "\"\n" +
                    "}";
            exchange.getResponseHeaders().set("Content-Type", "application/json");
            exchange.sendResponseHeaders(200, json.getBytes().length);
            OutputStream os = exchange.getResponseBody();
            os.write(json.getBytes());
            os.close();
        }

    }

    static class HistoryHandler implements HttpHandler {

        @Override
        public void handle(HttpExchange exchange) throws IOException {
            File file = new File("history.log");
            logger.log("HistoryHandler:handle - method started.");
            if (!file.exists()) {
                logger.log("HistoryHandler:handle - file doesn't exist.");
                System.err.println("src/history.log doesn't exist");
                String empty = "[]";
                exchange.getResponseHeaders().set("Content-Type", "application/json");
                exchange.sendResponseHeaders(200, empty.length());
                exchange.getResponseBody().write(empty.getBytes());
                exchange.getResponseBody().close();
                return;
            }
            logger.log("HistoryHandler:handle - File found.");

            List<String> lines = Files.readAllLines(file.toPath());
            logger.log("HistoryHandler:handle - # lines = " + lines.size());
            List<String> jsonRecords = lines.stream().map(GreetingRecord::fromLogLine).map(GreetingRecord::toJson)
                    .collect(Collectors.toList());
            logger.log("HistoryHandler:handle - " + jsonRecords);

            String json = "[\n" + String.join(",\n", jsonRecords) + "\n]";
            logger.log("HistoryHandler:handle json values - " + json);
            exchange.getResponseHeaders().set("Content-Type", "application/json");
            exchange.sendResponseHeaders(200, json.getBytes().length);
            exchange.getResponseBody().write(json.getBytes());
            exchange.getResponseBody().close();
        }

    }

    static class MetricsHandler implements HttpHandler {

        @Override
        public void handle(HttpExchange exchange) throws IOException {
            File file = new File("history.log");
            Map<String, Long> languageCounts = Files.exists(file.toPath())
                    ? Files.lines(file.toPath())
                            .map(line -> line.split("\\|")[0])
                            .collect(Collectors.groupingBy(lang -> lang, Collectors.counting()))
                    : Map.of();

            StringBuilder json = new StringBuilder("{\n");
            int i = 0;
            int size = languageCounts.size();
            for (Map.Entry<String, Long> entry : languageCounts.entrySet()) {
                json.append("  \"").append(entry.getKey()).append("\": ").append(entry.getValue());
                if (++i < size)
                    json.append(",\n");
                else
                    json.append("\n");
            }
            json.append("}");

            byte[] bytes = json.toString().getBytes(StandardCharsets.UTF_8);
            exchange.getResponseHeaders().set("Content-Type", "application/json");
            exchange.sendResponseHeaders(200, bytes.length);
            exchange.getResponseBody().write(bytes);
            exchange.getResponseBody().close();

        }

    }

    static class SwaggerHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            File file = new File("swagger.json");
            if (!file.exists()) {
                String msg = "{ \"error\": \"swagger.json not found\" }";
                exchange.sendResponseHeaders(404, msg.length());
                exchange.getResponseBody().write(msg.getBytes());
                exchange.getResponseBody().close();
                return;
            }
            byte[] bytes = Files.readAllBytes(file.toPath());
            exchange.getResponseHeaders().set("Content-Type", "application/json");
            exchange.sendResponseHeaders(200, bytes.length);
            exchange.getResponseBody().write(bytes);
            exchange.getResponseBody().close();
        }
    }

    private static String upTime() {
        long uptimeMillis = System.currentTimeMillis() - START_TIME;
        long seconds = (uptimeMillis / 1000) % 60;
        long minutes = (uptimeMillis / (1000 * 60)) % 60;
        long hours = uptimeMillis / (1000 * 60 * 60);
        return String.format("%dh %dm %ds", hours, minutes, seconds);
    }

    private static List<String> getMemoryStats() {
        Runtime runtime = Runtime.getRuntime();
        long total = runtime.totalMemory();
        long free = runtime.freeMemory();
        long used = total - free;
        String usedMb = used / (1024 * 1024) + " MB";
        String freeMb = free / (1024 * 1024) + " MB";
        String totalMb = total / (1024 * 1024) + " MB";
        return List.of(usedMb, freeMb, totalMb);
    }
}
