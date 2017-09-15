package net.pl3x.forge.discord;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import net.minecraftforge.fml.common.event.FMLServerStoppingEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.pl3x.forge.discord.configuration.ConfigWatcher;
import net.pl3x.forge.discord.configuration.Configuration;
import net.pl3x.forge.discord.configuration.Lang;
import net.pl3x.forge.discord.listener.DiscordListener;
import net.pl3x.forge.discord.listener.MinecraftListener;
import net.pl3x.forge.discord.scheduler.Pl3xScheduler;

import java.io.File;

@Mod(modid = DiscordBot.modId, name = DiscordBot.name, version = DiscordBot.version,
        serverSideOnly = true, acceptableRemoteVersions = "*")
public class DiscordBot {
    public static final String modId = "discordbot";
    public static final String name = "DiscordBot";
    public static final String version = "@VERSION@";

    private static final Pl3xScheduler pl3xScheduler = new Pl3xScheduler();
    private static Client client = new Client();

    private Thread configWatcher;

    public static Pl3xScheduler getScheduler() {
        return pl3xScheduler;
    }

    public static Client getClient() {
        return client;
    }

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        File configDir = new File(event.getModConfigurationDirectory(), DiscordBot.name);

        Configuration.reload(configDir);
        Lang.reload(configDir);

        configWatcher = new Thread(new ConfigWatcher(configDir.toPath()), Lang.colorize("&1Config&r"));
        configWatcher.start();
    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {
        MinecraftForge.EVENT_BUS.register(new MinecraftListener());
    }

    @Mod.EventHandler
    public void serverStarting(FMLServerStartingEvent event) {
        client.connect();
        client.addEventListener(new DiscordListener());
    }

    @Mod.EventHandler
    public void serverStopping(FMLServerStoppingEvent event) {
        configWatcher.interrupt();
        client.sendToDiscord(Lang.getData().SERVER_STOPPED);
        client.disconnect();
    }

    @Mod.EventBusSubscriber
    public static class EventHandler {
        @SubscribeEvent
        public static void serverTick(TickEvent.ServerTickEvent event) {
            if (event.phase == TickEvent.Phase.START) {
                pl3xScheduler.tick();
            }
        }
    }
}
