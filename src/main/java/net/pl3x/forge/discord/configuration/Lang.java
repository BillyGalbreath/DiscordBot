package net.pl3x.forge.discord.configuration;


import net.minecraft.command.ICommandSender;
import net.minecraft.util.text.TextComponentString;
import net.pl3x.forge.discord.DiscordBot;
import net.pl3x.forge.discord.Logger;
import net.pl3x.forge.discord.util.ChatColor;

import java.io.File;
import java.io.IOException;

public class Lang extends ConfigLoader implements ConfigBase {
    public static final Lang INSTANCE = new Lang();
    private static final String FILE_NAME = "messages.json";

    public Data data;

    public void init() {
        reload();
    }

    public String file() {
        return FILE_NAME;
    }

    public void reload() {
        Logger.info("Loading " + FILE_NAME + " from disk...");
        try {
            data = loadConfig(new Data(), Data.class, new File(DiscordBot.configDir, FILE_NAME));
        } catch (IOException e) {
            data = null;
            e.printStackTrace();
        }
    }

    public static void send(ICommandSender sender, String message) {
        if (message != null && !message.isEmpty()) {
            for (String part : message.split("\n")) {
                sender.sendMessage(new TextComponentString(ChatColor.colorize(part)));
            }
        }
    }

    public static class Data {
        public String SERVER_STARTED = ":white_check_mark: **Server has started**";
        public String SERVER_STOPPED = ":octagonal_sign: **Server has stopped**";
        public String MINECRAFT_CHAT_PREFIX = "&7[&3D&7]";
        public String ADVANCEMENT_ICON_TASK = ":medal:";
        public String ADVANCEMENT_ICON_CHALLENGE = ":military_medal:";
        public String ADVANCEMENT_ICON_GOAL = ":trophy:";
        public String ADVANCEMENT_MESSAGE = "{icon} {player} {type} **[**{title}**]**```{title}:\n  {description}```";
        public String DEATH_MESSAGE = ":skull_crossbones: **{message}**";
        public String JOIN_MESSAGE = "<:plus:343016812872466432> **{player} joined the game**";
        public String JOIN_NAME_CHANGED_MESSAGE = "<:plus:343016812872466432> **{player} (formerly known as {oldname}) joined the game**";
        public String JOIN_FIRST_TIME_MESSAGE = ":tada: **{player} joined the game for the first time**";
        public String QUIT_MESSAGE = "<:minus:343016832145293312> **{player} left the game**";
    }
}
