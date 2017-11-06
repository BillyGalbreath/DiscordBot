package net.pl3x.forge.discord;

import net.dv8tion.jda.core.AccountType;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.JDABuilder;
import net.dv8tion.jda.core.entities.Game;
import net.dv8tion.jda.core.entities.SelfUser;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.events.ReadyEvent;
import net.dv8tion.jda.core.hooks.EventListener;
import net.dv8tion.jda.core.hooks.ListenerAdapter;
import net.minecraftforge.fml.server.FMLServerHandler;
import net.pl3x.forge.discord.configuration.DiscordConfig;
import net.pl3x.forge.discord.configuration.Lang;
import net.pl3x.forge.discord.util.WebhookUtil;

import javax.security.auth.login.LoginException;
import java.util.ArrayList;

public class Client extends ListenerAdapter {
    private ArrayList<EventListener> eventListeners = new ArrayList<>();
    private JDA jda;

    @Override
    public void onReady(ReadyEvent event) {
        Logger.info("Logged in as " + getSelf().getName());

        jda.getPresence().setGame(Game.of("Minecraft " + FMLServerHandler.instance().getServer().getMinecraftVersion()));

        // skin connection check because bot _is_ connected, but status doesnt say so yet
        sendToDiscord(false, null, Lang.INSTANCE.data.SERVER_STARTED);
    }

    public void connect() {
        if (this.jda != null) {
            Logger.error("Is already connected");
            return;
        }

        String token = DiscordConfig.INSTANCE.data.getBotToken();
        if (token == null || token.isEmpty()) {
            Logger.error("Missing token");
            return;
        }

        try {
            JDABuilder builder = new JDABuilder(AccountType.BOT)
                    .setToken(token)
                    .setAudioEnabled(false)
                    .setBulkDeleteSplittingEnabled(false)
                    .addEventListener(this);

            for (EventListener eventListener : eventListeners) {
                builder.addEventListener(eventListener);
            }

            jda = builder.buildAsync();
        } catch (LoginException e) {
            Logger.error("Failed to connect to Discord: " + e.getMessage());
        } catch (Exception e) {
            Logger.error("Failed to connect to Discord");
            e.printStackTrace();
        }
    }

    public void addEventListener(EventListener eventListener) {
        if (eventListener != null) {
            if (eventListeners.contains(eventListener)) {
                return;
            }
            eventListeners.add(eventListener);
            if (jda != null) {
                jda.addEventListener(eventListener);
            }
        }
    }

    public void removeEventListener(EventListener eventListener) {
        if (eventListener != null) {
            if (!eventListeners.contains(eventListener)) {
                return;
            }
            eventListeners.remove(eventListener);
            if (jda != null) {
                jda.removeEventListener(eventListener);
            }
        }
    }

    public boolean isConnected() {
        return jda != null && jda.getStatus() == JDA.Status.CONNECTED;
    }

    public void disconnect() {
        if (this.jda == null) {
            Logger.error("Is already disconnected");
            return;
        }

        jda.shutdown();
        Logger.info("Disconnected from Discord");
        jda = null;
    }

    public JDA getJda() {
        return jda;
    }

    public SelfUser getSelf() {
        if (jda == null) {
            return null;
        }
        return jda.getSelfUser();
    }

    public User getUser(long userId) {
        if (jda == null) {
            return null;
        }
        return jda.getUserById(userId);
    }

    public void sendToDiscord(String message) {
        sendToDiscord(null, message);
    }

    public void sendToDiscord(String sender, String message) {
        sendToDiscord(true, sender, message);
    }

    public void sendToDiscord(boolean checkConnected, String sender, String message) {
        if (message == null || message.isEmpty()) {
            return;
        }

        if (checkConnected && !isConnected()) {
            return;
        }

        long channelId = DiscordConfig.INSTANCE.data.getChannelID();
        TextChannel channel = jda.getTextChannelById(channelId);
        if (channel == null) {
            Logger.error("Could not find channel " + channelId);
            return;
        }

        if (!channel.canTalk()) {
            Logger.error("Missing permission to write in channel " + channel.getName() + " (" + channelId + ")");
            return;
        }

        if (sender != null && !sender.isEmpty()) {
            WebhookUtil.sendMessage(channel, sender, message);
        } else {
            channel.sendMessage(message).queue();
        }
    }
}
