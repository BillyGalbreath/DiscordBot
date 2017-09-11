package net.pl3x.forge.discord.listener;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.common.UsernameCache;
import net.minecraftforge.event.ServerChatEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;
import net.pl3x.forge.discord.DiscordBot;
import net.pl3x.forge.discord.configuration.Lang;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class MinecraftListener {
    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void onChatMessage(ServerChatEvent event) {
        if (event.isCanceled() || event.getPlayer() == null) {
            return;
        }

        DiscordBot.getClient().sendToDiscord(true, event.getPlayer().getName(), event.getMessage());
    }

    /*@SubscribeEvent(priority = EventPriority.LOWEST)
    public void onPlayerAchievement(AchievementEvent event) {
        if (event.isCanceled()) {
            return;
        }
        //
    }*/

    private final Map<UUID, String> lastKnownNames = new HashMap<>();

    @SubscribeEvent(priority = EventPriority.HIGHEST) // happens FIRST
    public void onPlayerJoinHighest(PlayerEvent.PlayerLoggedInEvent event) {
        if (event.player != null) {
            lastKnownNames.put(event.player.getUniqueID(),
                    UsernameCache.getLastKnownUsername(event.player.getUniqueID()));
        }
    }

    @SubscribeEvent(priority = EventPriority.LOWEST) // happens LAST
    public void onPlayerJoinLowest(PlayerEvent.PlayerLoggedInEvent event) {
        if (event.player != null) {
            lastKnownNames.remove(event.player.getUniqueID());
        }
    }

    @SubscribeEvent(priority = EventPriority.NORMAL) // happens MIDDLE
    public void onPlayerJoin(PlayerEvent.PlayerLoggedInEvent event) {
        if (event.isCanceled() || event.player == null) {
            return;
        }

        String message;
        String lastKnownName = lastKnownNames.get(event.player.getUniqueID());
        if (lastKnownName == null || lastKnownName.isEmpty()) {
            message = Lang.JOIN_FIRST_TIME_MESSAGE;
        } else if (!lastKnownName.equals(event.player.getName())) {
            message = Lang.JOIN_NAME_CHANGED_MESSAGE
                    .replace("{oldname}", lastKnownName);
        } else {
            message = Lang.JOIN_MESSAGE;
        }

        message = message.replace("{player}", event.player.getName());

        DiscordBot.getClient().sendToDiscord(message);
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void onPlayerLeave(PlayerEvent.PlayerLoggedOutEvent event) {
        if (event.isCanceled() || event.player == null) {
            return;
        }

        String message = Lang.QUIT_MESSAGE
                .replace("{player}", event.player.getName());

        DiscordBot.getClient().sendToDiscord(message);
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void onPlayerDeath(LivingDeathEvent event) {
        if (!(event.getEntity() instanceof EntityPlayerMP)) {
            return;
        }

        String message = Lang.DEATH_MESSAGE
                .replace("{message}", ((EntityPlayerMP) event.getEntity())
                        .getCombatTracker().getDeathMessage().getUnformattedText());

        DiscordBot.getClient().sendToDiscord(message);
    }
}
