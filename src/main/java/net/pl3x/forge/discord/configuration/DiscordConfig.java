package net.pl3x.forge.discord.configuration;


import net.pl3x.forge.discord.DiscordBot;
import net.pl3x.forge.discord.Logger;

import java.io.File;
import java.io.IOException;

public class DiscordConfig extends ConfigLoader implements ConfigBase {
    public static final DiscordConfig INSTANCE = new DiscordConfig();
    private static final String FILE_NAME = "discord.json";


    public Data data;

    @Override
    public String file() {
        return FILE_NAME;
    }

    @Override
    public void init() {
        reload();
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

    public static class Data {
        private String botToken = "";
        private long channelID = 353713452453462017L;
        private String gameStatus = "Minecraft {version}";
        private String commandTrigger = "!";
        private String chatFormat = "&7[&3D&7]&r{sender}&e:&r {message}";

        Data() {
        }

        public String getBotToken() {
            return botToken;
        }

        public long getChannelID() {
            return channelID;
        }

        public String getGameStatus() {
            return gameStatus;
        }

        public String getCommandTrigger() {
            return commandTrigger;
        }

        public String getChatFormat() {
            return chatFormat;
        }
    }
}
