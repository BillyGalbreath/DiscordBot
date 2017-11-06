package net.pl3x.forge.discord.util;

import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.entities.Webhook;
import net.dv8tion.jda.core.requests.Request;
import net.dv8tion.jda.core.requests.Response;
import net.dv8tion.jda.core.requests.RestAction;
import net.dv8tion.jda.core.requests.Route;
import net.pl3x.forge.discord.DiscordBot;
import net.pl3x.forge.discord.Logger;
import net.pl3x.forge.discord.configuration.DiscordConfig;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class WebhookUtil {
    private static int currentWebhook = 0;
    private static String lastSender = null;

    static {
        try {
            // get rid of all previous webhooks created by DiscordBot if they don't match set channel
            for (Guild guild : DiscordBot.getClient().getJda().getGuilds()) {
                for (Webhook webhook : guild.getWebhooks().complete()) {
                    if (webhook.getName().startsWith("DiscordBot") &&
                            DiscordConfig.INSTANCE.data.getChannelID() !=
                                    webhook.getChannel().getIdLong()) {
                        webhook.delete().reason("Purge").queue();
                    }
                }
            }
        } catch (Exception e) {
            Logger.warn("Failed to purge already existing webhooks: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static void sendMessage(TextChannel channel, String sender, String message) {
        Webhook webhook = getWebhook(channel, sender);
        if (webhook == null) {
            return;
        }

        String avatarURL = "https://minotar.net/helm/" + sender + "/100.png";
        if (sender == null) {
            sender = DiscordBot.getClient().getSelf().getName();
            avatarURL = DiscordBot.getClient().getSelf().getAvatarUrl();
        }

        JSONObject json = new JSONObject();
        json.put("username", sender);
        json.put("avatar_url", avatarURL);
        json.put("content", message.length() > 2000 ? message.substring(0, 1997) + "..." : message);

        new RestAction<Void>(DiscordBot.getClient().getJda(),
                Route.Webhooks.EXECUTE_WEBHOOK.compile(webhook.getId(), webhook.getToken()), json) {
            protected void handleResponse(Response response, Request<Void> request) {
                try {
                    if (response.isOk()) {
                        request.onSuccess(null);
                    } else {
                        request.onFailure(response);
                    }
                } catch (Exception ignored) {
                }
            }
        }.queue();
    }

    private static Webhook getWebhook(TextChannel channel, String sender) {
        List<Webhook> webhooks = new ArrayList<>();
        channel.getGuild().getWebhooks().complete().stream()
                .filter(webhook -> webhook.getName().startsWith("DiscordBot #" + channel.getName() + " #"))
                .forEach(webhooks::add);

        if (webhooks.size() != 2 && !createWebhooks(channel, webhooks)) {
            return null; // unable to create webhooks
        }

        if ((sender != null && !sender.equals(lastSender)) ||
                (lastSender != null && !lastSender.equals(sender))) {
            lastSender = sender;
            currentWebhook = currentWebhook == 1 ? 0 : 1;
        }

        return webhooks.get(currentWebhook);
    }

    private static boolean createWebhooks(TextChannel channel, List<Webhook> webhooks) {
        webhooks.forEach(webhook -> webhook.delete().reason("Purging orphaned webhook").queue());
        webhooks.clear();

        if (!channel.getGuild().getMember(channel.getJDA().getSelfUser()).hasPermission(Permission.MANAGE_WEBHOOKS)) {
            Logger.error("Can't create a webhook to deliver chat message, bot is missing permission \"Manage Webhooks\"");
            return false;
        }

        Webhook webhook1 = createWebhook(channel.getGuild(), channel, "DiscordBot #" + channel.getName() + " #1");
        Webhook webhook2 = createWebhook(channel.getGuild(), channel, "DiscordBot #" + channel.getName() + " #2");
        if (webhook1 == null || webhook2 == null) {
            return false;
        }

        webhooks.add(webhook1);
        webhooks.add(webhook2);
        return true;
    }

    private static Webhook createWebhook(Guild guild, TextChannel channel, String name) {
        try {
            return guild.getController().createWebhook(channel, name).complete();
        } catch (Exception e) {
            Logger.error("Failed to create webhook " + name + " for message delivery: " + e.getMessage());
            return null;
        }
    }
}
