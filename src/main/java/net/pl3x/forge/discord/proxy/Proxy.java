package net.pl3x.forge.discord.proxy;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import net.minecraftforge.fml.common.event.FMLServerStoppingEvent;
import net.pl3x.forge.discord.DiscordBot;
import net.pl3x.forge.discord.configuration.ConfigWatcher;
import net.pl3x.forge.discord.configuration.Configuration;
import net.pl3x.forge.discord.configuration.Lang;
import net.pl3x.forge.discord.listener.DiscordListener;
import net.pl3x.forge.discord.listener.MinecraftListener;

import java.io.File;

public class Proxy {
    private Thread configWatcher;

    public void preInit(FMLPreInitializationEvent event) {
        File configDir = new File(event.getModConfigurationDirectory(), DiscordBot.name);

        configWatcher = new Thread(new ConfigWatcher(configDir.toPath()), Lang.colorize("&1Config&r"));
        configWatcher.start();

        Configuration.reload(configDir);
        Lang.reload(configDir);
    }

    public void init(FMLInitializationEvent event) {
        MinecraftForge.EVENT_BUS.register(new MinecraftListener());
    }

    public void postInit(FMLPostInitializationEvent event) {
        //
    }

    public void serverStarting(FMLServerStartingEvent event) {
        DiscordBot.getClient().connect();

        DiscordBot.getClient().addEventListener(new DiscordListener());
    }

    public void serverStopping(FMLServerStoppingEvent event) {
        configWatcher.interrupt();

        DiscordBot.getClient().sendToDiscord(Lang.SERVER_STOPPED);

        DiscordBot.getClient().disconnect();
    }
}
