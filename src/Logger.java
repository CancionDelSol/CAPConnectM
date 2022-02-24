import java.util.List;
import java.util.ArrayList;

public class Logger {
    // Save all lists until end, then save file
    private static final List<String> _messages = new ArrayList<>();


    public static void Info(String msg) {
        _messages.add("Info : ");
    }

    public static void Error(String msg) {
        _messages.add("Error: ");
    }

    public static void Warning(String msg) {
        _messages.add("Warn : ");
    }
}