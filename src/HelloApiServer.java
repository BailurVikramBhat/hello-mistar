
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

public class HelloApiServer {
    private static final AppLogger logger = new AppLogger();

    public static void start() throws IOException {
        logger.log("HelloApiServer:start - starting the server implementations");
        HttpServer server = HttpServer.create(new InetSocketAddress(8080), 0);
        server.createContext("/greet", new GreetHandler());
        server.createContext("/languages", new LanguageListHandler());
        logger.log("HelloApiServer:start - Successfully created context for /greet and /languages endpoints.");
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

            File pluginsDirectory = new File("src/plugins");
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

}
