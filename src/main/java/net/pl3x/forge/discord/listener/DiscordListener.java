package net.pl3x.forge.discord.listener;

import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.management.PlayerList;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.ServerChatEvent;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.pl3x.forge.discord.DiscordBot;
import net.pl3x.forge.discord.configuration.Configuration;
import net.pl3x.forge.discord.configuration.Lang;
import net.pl3x.forge.discord.util.BotCommandSender;
import net.pl3x.forge.discord.util.DiscordFakePlayer;

public class DiscordListener extends ListenerAdapter {
    private static MinecraftServer serverInstance = FMLCommonHandler.instance().getMinecraftServerInstance();
    private static PlayerList playerList = serverInstance.getPlayerList();

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        if (event.getTextChannel().getIdLong() != Configuration.getConfig().getChannelID()) {
            return; // not our channel
        }

        if (event.getAuthor().isBot()) {
            return; // ignore bots
        }

        if (event.getAuthor().getId().equals(DiscordBot.getClient().getSelf().getId())) {
            return; // ignore self
        }

        String message = event.getMessage().getContent().trim();
        if (message.isEmpty()) {
            return; // cant send empty messages
        }

        String cmdTrigger = Configuration.getConfig().getCommandTrigger();
        if (message.startsWith(cmdTrigger) && message.length() > cmdTrigger.length() &&
                handleCommand(message.substring(cmdTrigger.length()))) {
            return; // handled command
        }

        handleChat(event.getAuthor().getName(), message);
    }

    private boolean handleCommand(String message) {
        String[] split = message.split(" ");
        if (split.length == 0 || split[0] == null || split[0].isEmpty()) {
            return false; // sanity check
        }

        // allowed commands
        switch (split[0]) {
            case "list":
            case "tps":
                break;
            default:
                return false;
        }

        return 0 < serverInstance.getCommandManager().executeCommand(
                new BotCommandSender(serverInstance), "/" + message);
    }

    private void handleChat(String sender, String message) {
        // we use a new thread here to mask JDA's long named thread b.s. in console/log output
        new Thread(() -> {
            // let chat plugin handle overall chat formatting
            ServerChatEvent event = new ServerChatEvent(new DiscordFakePlayer(serverInstance, sender), message,
                    new TextComponentTranslation("chat.type.text", sender, ForgeHooks.newChatWithLinks(message)));
            if (MinecraftForge.EVENT_BUS.post(event)) {
                return; // event cancelled
            }

            // broadcast message to minecraft with discord prefix
            playerList.sendMessage(new TextComponentString(Lang.colorize(Lang.getData().MINECRAFT_CHAT_PREFIX))
                    .appendSibling(event.getComponent()));
        }, "Server thread").start();
    }
}
