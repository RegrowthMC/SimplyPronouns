package org.lushplugins.simplypronouns.util;

import club.minnced.discord.webhook.WebhookClient;
import club.minnced.discord.webhook.send.WebhookEmbed;
import club.minnced.discord.webhook.send.WebhookEmbedBuilder;
import club.minnced.discord.webhook.send.WebhookMessageBuilder;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.lushplugins.lushlib.utils.Pair;
import org.lushplugins.simplypronouns.SimplyPronouns;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;
import java.util.logging.Level;

public class DiscordUtil {
    private static final HashMap<String, WebhookClient> CLIENTS = new HashMap<>();

    public static CompletableFuture<WebhookClient> getOrCreateClient(String webhookUrl) {
        CompletableFuture<WebhookClient> future = new CompletableFuture<>();

        Bukkit.getScheduler().runTaskAsynchronously(SimplyPronouns.getInstance(), () -> {
            future.complete(CLIENTS.computeIfAbsent(webhookUrl, (ignored) -> WebhookClient.withUrl(webhookUrl)));
        });

        return future;
    }

    public static void closeClients() {
        CLIENTS.values().forEach(WebhookClient::close);
        CLIENTS.clear();
    }

    public static Message createMessage(ConfigurationSection config) {
        return new Message(config);
    }

    public static class Message {
        private final String webhookUrl;
        private final @Nullable String message;
        private final @Nullable Embed embed;

        private Message(ConfigurationSection config) {
            this.webhookUrl = config.getString("webhook-url");
            if (webhookUrl == null || webhookUrl.isBlank()) {
                throw new NullPointerException("Discord Logging requires a valid Webhook URL to be specified");
            }

            this.message = config.getString("message");

            ConfigurationSection embedSection = config.getConfigurationSection("embed");
            this.embed = embedSection != null ? new Embed(embedSection) : null;
        }

        public void send(Function<String, String> stringModifier) {
            WebhookMessageBuilder messageBuilder = new WebhookMessageBuilder()
                .setContent(StringUtils.parseString(message, stringModifier));

            if (embed != null) {
                messageBuilder.addEmbeds(embed.parseEmbed(stringModifier));
            }

            getOrCreateClient(webhookUrl).thenAccept(client -> client.send(messageBuilder.build()));
        }
    }

    public static class Embed {
        private final @Nullable String title;
        private final @Nullable String titleUrl;
        private final @Nullable String description;
        private final @Nullable Integer color;
        private final @Nullable String author;
        private final @Nullable String authorUrl;
        private final @Nullable String footer;
        private final @Nullable String footerUrl;
        private final @Nullable String thumbnailUrl;
        private final @Nullable String imageUrl;
        private final @NotNull HashMap<String, Pair<String, Boolean>> fields;

        private Embed(ConfigurationSection config) {
            this.title = config.getString("title");
            this.titleUrl = config.getString("title-url");
            this.description = config.getString("description");
            this.color = Integer.parseInt(config.getString("color", "ffffff"), 16);
            this.author = config.getString("author");
            this.authorUrl = config.getString("author-url");
            this.footer = config.getString("footer");
            this.footerUrl = config.getString("footer-url");
            this.thumbnailUrl = config.getString("thumbnail");
            this.imageUrl = config.getString("image");

            this.fields = new HashMap<>();
            for (Map<?, ?> fieldMap : config.getMapList("fields")) {
                try {
                    String name = (String) fieldMap.get("name");
                    String value = (String) fieldMap.get("value");
                    boolean inline = fieldMap.containsKey("inline") && (boolean) fieldMap.get("inline");

                    this.fields.put(name, new Pair<>(value, inline));
                } catch (ClassCastException e) {
                    SimplyPronouns.getInstance().getLogger().log(Level.WARNING, "Caught error whilst parsing embed fields at " + config.getCurrentPath() + ": ", e);
                }
            }
        }

        public WebhookEmbed parseEmbed(Function<String, String> stringModifier) {
            WebhookEmbedBuilder embedBuilder = new WebhookEmbedBuilder()
                .setDescription(StringUtils.parseString(description, stringModifier))
                .setColor(color)
                .setThumbnailUrl(thumbnailUrl)
                .setImageUrl(imageUrl);

            if (title != null) {
                embedBuilder.setTitle(new WebhookEmbed.EmbedTitle(
                    StringUtils.parseString(title, stringModifier),
                    StringUtils.parseString(titleUrl, stringModifier)));
            }

            if (author != null) {
                embedBuilder.setAuthor(new WebhookEmbed.EmbedAuthor(
                    StringUtils.parseString(author, stringModifier),
                    StringUtils.parseString(authorUrl, stringModifier),
                    null));
            }

            if (footer != null) {
                embedBuilder.setFooter(new WebhookEmbed.EmbedFooter(
                    StringUtils.parseString(footer, stringModifier),
                    StringUtils.parseString(footerUrl, stringModifier)
                ));
            }

            fields.forEach((key, value) -> embedBuilder.addField(new WebhookEmbed.EmbedField(
                value.second(),
                StringUtils.parseString(key, stringModifier),
                StringUtils.parseString(value.first(), stringModifier))
            ));

            return embedBuilder.build();
        }
    }
}
