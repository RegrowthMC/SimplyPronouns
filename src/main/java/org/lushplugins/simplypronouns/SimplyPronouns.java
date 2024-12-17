package org.lushplugins.simplypronouns;

import org.lushplugins.simplypronouns.data.config.ConfigManager;
import org.lushplugins.simplypronouns.data.pronouns.PronounsManager;
import org.lushplugins.simplypronouns.data.user.UserManager;
import org.bukkit.plugin.java.JavaPlugin;

public final class SimplyPronouns extends JavaPlugin {
    private static SimplyPronouns plugin;
    private static ConfigManager configManager;
    private static PronounsManager pronounsManager;
    private static UserManager userManager;

    @Override
    public void onEnable() {
        plugin = this;
        configManager = new ConfigManager();
        pronounsManager = new PronounsManager();
        userManager = new UserManager();
    }

    @Override
    public void onDisable() {
        pronounsManager.disableIoHandlers();
        userManager.getIoHandler().disableIOHandler();
    }

    public static SimplyPronouns getInstance() {
        return plugin;
    }

    public static ConfigManager getConfigManager() {
        return configManager;
    }

    public static PronounsManager getPronounsManager() {
        return pronounsManager;
    }

    public static UserManager getUserManager() {
        return userManager;
    }
}
