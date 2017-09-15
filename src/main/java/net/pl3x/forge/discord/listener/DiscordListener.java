package net.pl3x.forge.discord.listener;

import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.management.PlayerList;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.server.FMLServerHandler;
import net.pl3x.forge.discord.BotCommandSender;
import net.pl3x.forge.discord.DiscordBot;
import net.pl3x.forge.discord.configuration.Configuration;
import net.pl3x.forge.discord.configuration.Lang;

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
        String chat = Lang.colorize(Lang.MINECRAFT_CHAT_FORMAT
                .replace("{sender}", sender));

        // replace the message content without color parsing!
        chat = chat.replace("{message}", message);

        // broadcast message to all online players
        ITextComponent component = new TextComponentString(chat);
        for (EntityPlayerMP player : playerList.getPlayers()) {
            player.sendMessage(component);
        }

        // log message to console like normal chat
        // we use a new thread here to mask JDA's long named thread b.s.
        new Thread(() -> FMLServerHandler.instance().getServer().sendMessage(component), "Server thread").start();
    }
}
