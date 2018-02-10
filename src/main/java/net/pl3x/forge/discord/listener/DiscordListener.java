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
import net.pl3x.forge.discord.configuration.DiscordConfig;
import net.pl3x.forge.discord.configuration.EmojiConfig;
import net.pl3x.forge.discord.configuration.Lang;
import net.pl3x.forge.discord.scheduler.Pl3xRunnable;
import net.pl3x.forge.discord.util.BotCommandSender;
import net.pl3x.forge.discord.util.ChatColor;
import net.pl3x.forge.discord.util.DiscordFakePlayer;

import java.util.regex.Matcher;

public class DiscordListener extends ListenerAdapter {
    private static MinecraftServer serverInstance = FMLCommonHandler.instance().getMinecraftServerInstance();
    private static PlayerList playerList = serverInstance.getPlayerList();

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        if (event.getTextChannel().getIdLong() != DiscordConfig.INSTANCE.data.getChannelID()) {
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

        String cmdTrigger = DiscordConfig.INSTANCE.data.getCommandTrigger();
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
        // get on the main thread
        DiscordBot.getScheduler().runTaskLater(new Pl3xRunnable() {
            @Override
            public void run() {
                final String msg = translateMessage(message);

                // let chat plugin handle overall chat formatting
                ServerChatEvent event = new ServerChatEvent(new DiscordFakePlayer(serverInstance, sender), msg,
                        new TextComponentTranslation("chat.type.text", sender, ForgeHooks.newChatWithLinks(msg)));
                if (MinecraftForge.EVENT_BUS.post(event)) {
                    return; // event cancelled
                }
                // broadcast message to minecraft with discord prefix
                playerList.sendMessage(new TextComponentString(ChatColor.colorize(Lang.INSTANCE.data.MINECRAFT_CHAT_PREFIX))
                        .appendSibling(event.getComponent()));
            }
        }, 1);
    }

    public String translateMessage(String message) {
        if (message == null) {
            return null;
        }

        Matcher match = EmojiConfig.EMOJI_TAG_REGEX_PATTERN.matcher(message);
        while (match.find()) {
            String code = match.group(1);
            if (code == null) {
                continue;
            }
            EmojiConfig.Emoji emoji = EmojiConfig.INSTANCE.data.getEmojis().stream()
                    .filter(e -> e.getAliases().contains(code.toLowerCase()))
                    .findFirst().orElse(null);
            if (emoji == null) {
                continue;
            }
            message = message.replace(":" + code + ":",
                    String.valueOf((char) Integer.parseInt(emoji.getHex(), 16)));
        }
        for (EmojiConfig.Emoji emoji : EmojiConfig.INSTANCE.data.getEmojis()) {
            message = message.replace(emoji.getEmoji(), String.valueOf((char) Integer.parseInt(emoji.getHex(), 16)));
        }
        return message;
    }
}
