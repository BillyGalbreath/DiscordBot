package net.pl3x.forge.discord;

import net.pl3x.forge.discord.configuration.Lang;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;

import static org.apache.logging.log4j.Level.ERROR;
import static org.apache.logging.log4j.Level.INFO;
import static org.apache.logging.log4j.Level.WARN;

public class Logger {
    private static org.apache.logging.log4j.Logger logger =
            LogManager.getLogger(Lang.colorize("&3DiscordBot&r"));

    private Logger() {
    }

    public static void log(Level level, String message) {
        logger.log(level, Lang.colorize(message));
    }

    public static void error(String message) {
        log(ERROR, "&c" + message);
    }

    public static void info(String message) {
        log(INFO, "&6" + message);
    }

    public static void warn(String message) {
        log(WARN, "&d" + message);
    }
}
