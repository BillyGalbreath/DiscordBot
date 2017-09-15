package net.pl3x.forge.discord.util;

import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.World;
import net.pl3x.forge.discord.DiscordBot;

import javax.annotation.Nullable;

public class BotCommandSender implements ICommandSender {
    private ICommandSender sender;

    public BotCommandSender(ICommandSender sender) {
        this.sender = sender;
    }

    @Override
    public String getName() {
        return sender.getName();
    }

    @Override
    public boolean canUseCommand(int permLevel, String commandName) {
        return sender.canUseCommand(permLevel, commandName);
    }

    @Override
    public World getEntityWorld() {
        return sender.getEntityWorld();
    }

    @Nullable
    @Override
    public MinecraftServer getServer() {
        return sender.getServer();
    }

    @Override
    public void sendMessage(ITextComponent component) {
        DiscordBot.getClient().sendToDiscord(component.getFormattedText().replaceAll("(?i)\u00a7([a-f0-9k-or])", ""));
    }
}
