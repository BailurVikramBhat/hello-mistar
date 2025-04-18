
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;

public class GreetingLogger {
    public static final String LOG_FILE = "greetings.log";
    public void log(String message) {
        try (FileWriter writer = new FileWriter(LOG_FILE, true)){
            String timestamp = LocalDateTime.now().toString();
            writer.write(timestamp + " - " +message+"\n");
            
        } catch (IOException e) {
            System.err.println("Failed to write log: "+ e.getMessage());
        }
    }

}
