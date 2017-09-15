package net.pl3x.forge.discord;

import com.mojang.authlib.GameProfile;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.common.util.FakePlayer;

import java.util.UUID;

public class DiscordFakePlayer extends FakePlayer {
    public DiscordFakePlayer(MinecraftServer minecraftServer, String name) {
        super(minecraftServer.worlds[0], new GameProfile(UUID.randomUUID(), name));
        this.dimension = Integer.MIN_VALUE;
    }
}
