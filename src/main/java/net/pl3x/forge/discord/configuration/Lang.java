package net.pl3x.forge.discord.configuration;


import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.text.TextComponentString;
import net.pl3x.forge.discord.Logger;

import java.io.File;
import java.io.IOException;

public class Lang extends ConfigLoader {
    public static String SERVER_STARTED;
    public static String SERVER_STOPPED;
    public static String MINECRAFT_CHAT_FORMAT;
    public static String ADVANCEMENT_MESSAGE;
    public static String DEATH_MESSAGE;
    public static String JOIN_MESSAGE;
    public static String JOIN_NAME_CHANGED_MESSAGE;
    public static String JOIN_FIRST_TIME_MESSAGE;
    public static String QUIT_MESSAGE;

    static final String FILE_NAME = "messages.json";
    private static File configDir;
    private static Data data;

    public static Data getConfig() {
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

        SERVER_STARTED = data.SERVER_STARTED;
        SERVER_STOPPED = data.SERVER_STOPPED;
        MINECRAFT_CHAT_FORMAT = data.MINECRAFT_CHAT_FORMAT;
        ADVANCEMENT_MESSAGE = data.ADVANCEMENT_MESSAGE;
        DEATH_MESSAGE = data.DEATH_MESSAGE;
        JOIN_MESSAGE = data.JOIN_MESSAGE;
        JOIN_NAME_CHANGED_MESSAGE = data.JOIN_NAME_CHANGED_MESSAGE;
        JOIN_FIRST_TIME_MESSAGE = data.JOIN_FIRST_TIME_MESSAGE;
        QUIT_MESSAGE = data.QUIT_MESSAGE;
    }

    public static void send(EntityPlayerMP player, String message) {
        for (String part : message.split("\n")) {
            player.sendMessage(new TextComponentString(colorize(part)));
        }
    }

    public static String colorize(String string) {
        return string.replaceAll("(?i)&([a-f0-9k-or])", "\u00a7$1");
    }

    public static class Data {
        public String SERVER_STARTED = ":white_check_mark: **Server has started**";
        public String SERVER_STOPPED = ":octagonal_sign: **Server has stopped**";
        public String MINECRAFT_CHAT_FORMAT = "[D]{sender}: {message}";
        public String ADVANCEMENT_MESSAGE = ":medal: {message}";
        public String DEATH_MESSAGE = ":skull: **{message}**";
        public String JOIN_MESSAGE = "**joined the game**";
        public String JOIN_NAME_CHANGED_MESSAGE = "**(formerly known as {oldname}) joined the game**";
        public String JOIN_FIRST_TIME_MESSAGE = "**joined the game for the first time**";
        public String QUIT_MESSAGE = "**left the game**";
    }
}
