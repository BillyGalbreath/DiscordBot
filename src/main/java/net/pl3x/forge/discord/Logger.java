package net.pl3x.forge.discord;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.fusesource.jansi.Ansi;

import static org.apache.logging.log4j.Level.ERROR;
import static org.apache.logging.log4j.Level.INFO;
import static org.apache.logging.log4j.Level.WARN;

public class Logger {
    private static org.apache.logging.log4j.Logger logger = LogManager.getLogger(colorizeConsole("&3DiscordBot&r"));

    private Logger() {
    }

    public static void log(Level level, String message) {
        logger.log(level, colorizeConsole(message));
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

    public static String colorizeConsole(String message) {
        message = message.replaceAll("(?i)&(0)", Ansi.ansi().a(Ansi.Attribute.RESET).fg(Ansi.Color.BLACK).boldOff().toString());
        message = message.replaceAll("(?i)&(1)", Ansi.ansi().a(Ansi.Attribute.RESET).fg(Ansi.Color.BLUE).boldOff().toString());
        message = message.replaceAll("(?i)&(2)", Ansi.ansi().a(Ansi.Attribute.RESET).fg(Ansi.Color.GREEN).boldOff().toString());
        message = message.replaceAll("(?i)&(3)", Ansi.ansi().a(Ansi.Attribute.RESET).fg(Ansi.Color.CYAN).boldOff().toString());
        message = message.replaceAll("(?i)&(4)", Ansi.ansi().a(Ansi.Attribute.RESET).fg(Ansi.Color.RED).boldOff().toString());
        message = message.replaceAll("(?i)&(5)", Ansi.ansi().a(Ansi.Attribute.RESET).fg(Ansi.Color.MAGENTA).boldOff().toString());
        message = message.replaceAll("(?i)&(6)", Ansi.ansi().a(Ansi.Attribute.RESET).fg(Ansi.Color.YELLOW).boldOff().toString());
        message = message.replaceAll("(?i)&(7)", Ansi.ansi().a(Ansi.Attribute.RESET).fg(Ansi.Color.WHITE).boldOff().toString());
        message = message.replaceAll("(?i)&(8)", Ansi.ansi().a(Ansi.Attribute.RESET).fg(Ansi.Color.BLACK).bold().toString());
        message = message.replaceAll("(?i)&(9)", Ansi.ansi().a(Ansi.Attribute.RESET).fg(Ansi.Color.BLUE).bold().toString());
        message = message.replaceAll("(?i)&(a)", Ansi.ansi().a(Ansi.Attribute.RESET).fg(Ansi.Color.GREEN).bold().toString());
        message = message.replaceAll("(?i)&(b)", Ansi.ansi().a(Ansi.Attribute.RESET).fg(Ansi.Color.CYAN).bold().toString());
        message = message.replaceAll("(?i)&(c)", Ansi.ansi().a(Ansi.Attribute.RESET).fg(Ansi.Color.RED).bold().toString());
        message = message.replaceAll("(?i)&(d)", Ansi.ansi().a(Ansi.Attribute.RESET).fg(Ansi.Color.MAGENTA).bold().toString());
        message = message.replaceAll("(?i)&(e)", Ansi.ansi().a(Ansi.Attribute.RESET).fg(Ansi.Color.YELLOW).bold().toString());
        message = message.replaceAll("(?i)&(f)", Ansi.ansi().a(Ansi.Attribute.RESET).fg(Ansi.Color.WHITE).bold().toString());
        message = message.replaceAll("(?i)&(k)", Ansi.ansi().a(Ansi.Attribute.BLINK_SLOW).toString());
        message = message.replaceAll("(?i)&(l)", Ansi.ansi().a(Ansi.Attribute.UNDERLINE_DOUBLE).toString());
        message = message.replaceAll("(?i)&(m)", Ansi.ansi().a(Ansi.Attribute.STRIKETHROUGH_ON).toString());
        message = message.replaceAll("(?i)&(n)", Ansi.ansi().a(Ansi.Attribute.UNDERLINE).toString());
        message = message.replaceAll("(?i)&(o)", Ansi.ansi().a(Ansi.Attribute.ITALIC).toString());
        message = message.replaceAll("(?i)&(r)", Ansi.ansi().a(Ansi.Attribute.RESET).toString());
        return message;
    }
}
