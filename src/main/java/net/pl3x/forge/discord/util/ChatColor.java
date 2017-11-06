package net.pl3x.forge.discord.util;

import com.google.common.collect.Maps;
import net.minecraft.util.text.TextFormatting;

import java.util.Map;
import java.util.regex.Pattern;

public enum ChatColor {
    BLACK('0'),
    DARK_BLUE('1'),
    DARK_GREEN('2'),
    DARK_AQUA('3'),
    DARK_RED('4'),
    DARK_PURPLE('5'),
    GOLD('6'),
    GRAY('7'),
    DARK_GRAY('8'),
    BLUE('9'),
    GREEN('a'),
    AQUA('b'),
    RED('c'),
    LIGHT_PURPLE('d'),
    YELLOW('e'),
    WHITE('f'),
    MAGIC('k', true),
    BOLD('l', true),
    STRIKETHROUGH('m', true),
    UNDERLINE('n', true),
    ITALIC('o', true),
    RESET('r');

    public static final char COLOR_CHAR = '\u00A7';
    public static final Pattern COLOR_PATTERN = Pattern.compile("(?i)" + COLOR_CHAR + "([0-9A-FR])");
    public static final Pattern FORMAT_PATTERN = Pattern.compile("(?i)" + COLOR_CHAR + "([k-or])");
    public static final Pattern ALTERNATE_COLOR_PATTERN = Pattern.compile("(?i)&([0-9A-FR])");
    public static final Pattern ALTERNATE_FORMAT_PATTERN = Pattern.compile("(?i)&([k-or])");
    private final static Map<Character, ChatColor> BY_CHAR = Maps.newHashMap();

    static {
        for (ChatColor color : values()) {
            BY_CHAR.put(color.code, color);
        }
    }

    private final char code;
    private final boolean isFormat;
    private final String toString;

    ChatColor(char code) {
        this(code, false);
    }

    ChatColor(char code, boolean isFormat) {
        this.code = code;
        this.isFormat = isFormat;
        this.toString = new String(new char[]{COLOR_CHAR, code});
    }

    public static String colorize(String string) {
        return TextFormatting.ALTERNATE_CODE_PATTERN.matcher(string).replaceAll(COLOR_CHAR + "$1");
    }

    public static String colorsOnly(String string) {
        return COLOR_PATTERN.matcher(string).replaceAll(COLOR_CHAR + "$1");
    }

    public static String formatOnly(String string) {
        return FORMAT_PATTERN.matcher(string).replaceAll(COLOR_CHAR + "$1");
    }

    public static String stripAllCodes(String string) {
        return TextFormatting.FORMATTING_CODE_PATTERN.matcher(TextFormatting.ALTERNATE_CODE_PATTERN.matcher(string).replaceAll(COLOR_CHAR + "$1")).replaceAll("");
    }

    public static String stripOnlyColors(String string) {
        return COLOR_PATTERN.matcher(ALTERNATE_COLOR_PATTERN.matcher(string).replaceAll(COLOR_CHAR + "$1")).replaceAll("");
    }

    public static String stripOnlyFormats(String string) {
        return FORMAT_PATTERN.matcher(ALTERNATE_FORMAT_PATTERN.matcher(string).replaceAll(COLOR_CHAR + "$1")).replaceAll("");
    }

    public static String getLastColors(String input) {
        String result = "";
        int length = input.length();

        // Search backwards from the end as it is faster
        for (int index = length - 1; index > -1; index--) {
            char section = input.charAt(index);
            if ((section == COLOR_CHAR || section == '&') && index < length - 1) {
                char c = input.charAt(index + 1);
                ChatColor color = BY_CHAR.get(c);

                if (color != null) {
                    result = color.toString() + result;

                    // Once we find a color or reset we can stop searching
                    if (color.isColor() || color.equals(RESET)) {
                        break;
                    }
                }
            }
        }

        return result;
    }

    @Override
    public String toString() {
        return toString;
    }

    public boolean isFormat() {
        return isFormat;
    }

    public boolean isColor() {
        return !isFormat() && this != RESET;
    }
}
