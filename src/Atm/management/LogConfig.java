package Atm.management;

import java.util.logging.ConsoleHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

public class LogConfig {
    public static void configure() {
        Logger rootLogger = Logger.getLogger("");
        rootLogger.setLevel(Level.WARNING);
        ConsoleHandler handler = new ConsoleHandler();
        handler.setLevel(Level.WARNING);
        rootLogger.addHandler(handler);
    }
} 