package net.pl3x.forge.discord.configuration;


import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.text.TextComponentString;
import net.pl3x.forge.discord.Logger;

import java.io.File;
import java.io.IOException;

public class Lang extends ConfigLoader {
    static final String FILE_NAME = "messages.json";
    private static File configDir;
    private static Data data;

    public static Data getData() {
        return data;
    }

    public static void reload(File dir) {
        configDir = dir;
        reload();
    }

    public static void reload() {
        Logger.info("Loading " + FILE_NAME + " from disk...");
        try {
            data = loadConfig(new Data(), Data.class, new File(configDir, FILE_NAME));
        } catch (IOException e) {
            data = null;
            e.printStackTrace();
        }
    }

    public static void send(EntityPlayerMP player, String message) {
        for (String part : message.split("\n")) {
            player.sendMessage(new TextComponentString(colorize(part)));
        }
    }

    public static String colorize(String string) {
        return string.replaceAll("(?i)&([a-f0-9k-or])", "\u00a7$1");
    }

    public static String stripColor(String string) {
        return string.replaceAll("(?i)\u00a7([a-f0-9k-or])", "");
    }

    public static class Data {
        public String SERVER_STARTED = ":white_check_mark: **Server has started**";
        public String SERVER_STOPPED = ":octagonal_sign: **Server has stopped**";
        public String MINECRAFT_CHAT_PREFIX = "&7[&3D&7]";
        public String ADVANCEMENT_ICON_TASK = ":medal:";
        public String ADVANCEMENT_ICON_CHALLENGE = ":military_medal:";
        public String ADVANCEMENT_ICON_GOAL = ":trophy:";
        public String ADVANCEMENT_MESSAGE = "{icon} {player} {type} **[**{title}**]**```{title}:\n{description}```";
        public String DEATH_MESSAGE = ":skull_crossbones: **{message}**";
        public String JOIN_MESSAGE = ":heavy_plus_sign: **{player} joined the game**";
        public String JOIN_NAME_CHANGED_MESSAGE = ":heavy_plus_sign: **{player} (formerly known as {oldname}) joined the game**";
        public String JOIN_FIRST_TIME_MESSAGE = ":heavy_plus_sign: **{player} joined the game for the first time**";
        public String QUIT_MESSAGE = ":heavy_minus_sign: **{player} left the game**";
    }
}
