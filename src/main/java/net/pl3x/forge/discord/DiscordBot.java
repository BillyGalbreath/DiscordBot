package net.pl3x.forge.discord;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import net.minecraftforge.fml.common.event.FMLServerStoppingEvent;
import net.pl3x.forge.discord.proxy.Proxy;

@Mod(modid = DiscordBot.modId, name = DiscordBot.name, version = DiscordBot.version,
        serverSideOnly = true, acceptableRemoteVersions = "*", dependencies = "after:pl3xserver")
public class DiscordBot {
    public static final String modId = "discordbot";
    public static final String name = "DiscordBot";
    public static final String version = "@VERSION@";

    private static Proxy proxy = new Proxy();
    private static Client client;

    public static Client getClient() {
        if (client == null) {
            client = new Client();
        }
        return client;
    }

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        proxy.preInit(event);
    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {
        proxy.init(event);
    }

    @Mod.EventHandler
    public void postInit(FMLPostInitializationEvent event) {
        proxy.postInit(event);
    }

    @Mod.EventHandler
    public void serverStarting(FMLServerStartingEvent event) {
        proxy.serverStarting(event);
    }

    @Mod.EventHandler
    public void serverStopping(FMLServerStoppingEvent event) {
        proxy.serverStopping(event);
    }
}
