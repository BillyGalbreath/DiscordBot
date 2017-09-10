package net.pl3x.forge.discord.listener;

import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.management.PlayerList;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.fml.common.FMLCommonHandler;
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

        String message = Lang.colorize(Lang.MINECRAFT_CHAT_FORMAT
                .replace("{sender}", event.getAuthor().getName()));

        // replace the message content without color parsing!
        message = message
                .replace("{message}", event.getMessage().getContent().trim());

        ITextComponent component = new TextComponentString(message);

        for (EntityPlayerMP player : playerList.getPlayers()) {
            player.sendMessage(component);
        }
    }
}
