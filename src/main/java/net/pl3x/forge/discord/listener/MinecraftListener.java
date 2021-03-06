package net.pl3x.forge.discord.listener;

import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.DisplayInfo;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.JsonToNBT;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.event.HoverEvent;
import net.minecraft.util.text.translation.I18n;
import net.minecraftforge.common.UsernameCache;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.event.ServerChatEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.player.AdvancementEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;
import net.pl3x.forge.discord.DiscordBot;
import net.pl3x.forge.discord.configuration.EmojiConfig;
import net.pl3x.forge.discord.configuration.Lang;
import net.pl3x.forge.discord.util.ChatColor;
import net.pl3x.forge.discord.util.ItemUtil;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class MinecraftListener {
    private final Map<UUID, String> lastKnownNames = new HashMap<>();

    @SubscribeEvent
    public void onAdvancement(AdvancementEvent event) {
        EntityPlayer player = event.getEntityPlayer();
        Advancement advancement = event.getAdvancement();

        if (advancement.getDisplay() == null ||
                !advancement.getDisplay().shouldAnnounceToChat() ||
                !player.world.getGameRules().getBoolean("announceAdvancements")) {
            return; // do not display this advancement
        }

        DisplayInfo display = advancement.getDisplay();

        String playerName = player.getDisplayName().getFormattedText();

        String frame = display.getFrame().getName();

        String title = display.getTitle().getFormattedText();
        String desc = display.getDescription().getFormattedText();

        String type = String.format(I18n.translateToLocal("chat.type.advancement." + frame), "", "").trim();

        String icon;
        switch (frame) {
            case "challenge":
                icon = Lang.INSTANCE.data.ADVANCEMENT_ICON_CHALLENGE;
                break;
            case "goal":
                icon = Lang.INSTANCE.data.ADVANCEMENT_ICON_GOAL;
                break;
            default:
                icon = Lang.INSTANCE.data.ADVANCEMENT_ICON_TASK;
        }

        DiscordBot.getClient().sendToDiscord(ChatColor.stripAllCodes(translateMessage(Lang.INSTANCE.data.ADVANCEMENT_MESSAGE
                .replace("{icon}", icon)
                .replace("{player}", playerName)
                .replace("{type}", type)
                .replace("{title}", title)
                .replace("{description}", desc)
        )));
    }

    @SubscribeEvent(priority = EventPriority.LOWEST) // happens LAST
    public void onChatMessage(ServerChatEvent event) {
        if (event.isCanceled() || event.getPlayer() == null ||
                event.getPlayer() instanceof FakePlayer) {
            return;
        }

        String itemName = "";
        String itemTooltip = "";

        ITextComponent component = event.getComponent();
        for (ITextComponent sibling : component.getSiblings()) {
            HoverEvent hover = sibling.getStyle().getHoverEvent();
            if (hover != null && hover.getAction() == HoverEvent.Action.SHOW_ITEM) {
                itemName = sibling.getFormattedText();
                try {
                    itemTooltip = String.join("\n", ItemUtil.getTooltip(event.getPlayer(),
                            new ItemStack(JsonToNBT.getTagFromJson(hover.getValue().getUnformattedText()))));
                } catch (Exception ignore) {
                    itemTooltip = "Invalid Item!";
                }
                break; // stop looking
            }
        }

        String text = event.getMessage();
        if (!itemName.isEmpty() && !itemTooltip.isEmpty()) {
            text += "```" + itemTooltip + "```";
        }

        DiscordBot.getClient().sendToDiscord(event.getPlayer().getName(),
                ChatColor.stripAllCodes(translateMessage(text).replaceAll("\\[item]", itemName)));
    }

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
            message = Lang.INSTANCE.data.JOIN_FIRST_TIME_MESSAGE;
        } else if (!lastKnownName.equals(event.player.getName())) {
            message = Lang.INSTANCE.data.JOIN_NAME_CHANGED_MESSAGE
                    .replace("{oldname}", lastKnownName);
        } else {
            message = Lang.INSTANCE.data.JOIN_MESSAGE;
        }

        message = message.replace("{player}", event.player.getName());

        DiscordBot.getClient().sendToDiscord(translateMessage(message));
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void onPlayerLeave(PlayerEvent.PlayerLoggedOutEvent event) {
        if (event.isCanceled() || event.player == null) {
            return;
        }

        String message = Lang.INSTANCE.data.QUIT_MESSAGE
                .replace("{player}", event.player.getName());

        DiscordBot.getClient().sendToDiscord(translateMessage(message));
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void onPlayerDeath(LivingDeathEvent event) {
        if (!(event.getEntity() instanceof EntityPlayerMP)) {
            return;
        }

        String message = Lang.INSTANCE.data.DEATH_MESSAGE
                .replace("{message}", ((EntityPlayerMP) event.getEntity())
                        .getCombatTracker().getDeathMessage().getUnformattedText());

        DiscordBot.getClient().sendToDiscord(translateMessage(message));
    }

    public String translateMessage(String message) {
        if (message == null) {
            return null;
        }
        StringBuilder sb = new StringBuilder();
        for (char c : message.toCharArray()) {
            EmojiConfig.Emoji emoji = EmojiConfig.INSTANCE.data.getEmoji(c);
            if (emoji == null) {
                sb.append(c);
                continue;
            }
            sb.append(emoji.getEmoji());
        }

        return sb.toString();
    }
}
