package org.lushplugins.simplypronouns.config;

import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.lushplugins.lushlib.utils.YamlUtils;
import org.lushplugins.simplypronouns.SimplyPronouns;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ConfigManager {
    private String placeholderFormat;
    private boolean requireApproval;
    private final ConcurrentHashMap<String, String> messages = new ConcurrentHashMap<>();

    public ConfigManager() {
        SimplyPronouns.getInstance().saveDefaultConfig();
    }

    public void reloadConfig() {
        SimplyPronouns.getInstance().reloadConfig();
        FileConfiguration config = SimplyPronouns.getInstance().getConfig();

        this.placeholderFormat = config.getString("placeholder-format");
        this.requireApproval = config.getBoolean("require-approval");

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

    public @Nullable String getMessage(String key) {
        return messages.get(key);
    }

    public @NotNull String getMessage(String key, @NotNull String def) {
        return messages.getOrDefault(key, def);
    }
}
