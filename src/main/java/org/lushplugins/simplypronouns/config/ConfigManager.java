package org.lushplugins.simplypronouns.config;

import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.lushplugins.lushlib.utils.YamlUtils;
import org.lushplugins.simplypronouns.SimplyPronouns;
import org.bukkit.configuration.file.FileConfiguration;
import org.lushplugins.simplypronouns.util.DiscordUtil;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;

public class ConfigManager {
    private String placeholderFormat;
    private boolean requireApproval;
    private HashMap<String, DiscordUtil.Message> discordMessages;
    private final ConcurrentHashMap<String, String> messages = new ConcurrentHashMap<>();

    public ConfigManager() {
        SimplyPronouns.getInstance().saveDefaultConfig();
    }

    public void reloadConfig() {
        SimplyPronouns.getInstance().reloadConfig();
        FileConfiguration config = SimplyPronouns.getInstance().getConfig();

        this.placeholderFormat = config.getString("placeholder-format");
        this.requireApproval = config.getBoolean("require-approval");

        this.discordMessages = new HashMap<>();
        ConfigurationSection discordMessagesSection = config.getConfigurationSection("messages");
        if (discordMessagesSection != null) {
            discordMessagesSection.getValues(false).forEach((key, value) -> {
                if (value instanceof ConfigurationSection discordMessageConfig) {
                    try {
                        this.discordMessages.put(key, DiscordUtil.createMessage(discordMessageConfig));
                    } catch (NullPointerException e) {
                        if (e.getMessage().contains("Webhook URL")) {
                            SimplyPronouns.getInstance().getLogger().log(Level.WARNING, e.getMessage());
                        } else {
                            SimplyPronouns.getInstance().getLogger().log(Level.WARNING, "Caught error when parsing 'discord-logging'", e);
                        }
                    }
                }
            });
        }

        ConfigurationSection messagesSection = config.getConfigurationSection("messages");
        if (messagesSection != null) {
            for (Map.Entry<String, Object> entry : messagesSection.getValues(false).entrySet()) {
                if (entry.getValue() instanceof String message) {
                    messages.put(entry.getKey(), message);
                }
            }
        }
    }

    public String getPlaceholderFormat() {
        return placeholderFormat;
    }

    public boolean requiresApproval() {
        return requireApproval;
    }

    public @Nullable DiscordUtil.Message getDiscordLog(String key) {
        return discordMessages.get(key);
    }

    public @Nullable String getMessage(String key) {
        return messages.get(key);
    }

    public @NotNull String getMessage(String key, @NotNull String def) {
        return messages.getOrDefault(key, def);
    }
}
