package org.lushplugins.simplypronouns.config;

import org.lushplugins.simplypronouns.SimplyPronouns;
import org.bukkit.configuration.file.FileConfiguration;

public class ConfigManager {
    private String placeholderFormat;
    private boolean requireApproval;

    public ConfigManager() {
        SimplyPronouns.getInstance().saveDefaultConfig();
    }

    public void reloadConfig() {
        SimplyPronouns.getInstance().reloadConfig();
        FileConfiguration config = SimplyPronouns.getInstance().getConfig();

        this.placeholderFormat = config.getString("placeholder-format");
        this.requireApproval = config.getBoolean("require-approval");
    }

    public String getPlaceholderFormat() {
        return placeholderFormat;
    }

    public boolean requiresApproval() {
        return requireApproval;
    }
}
