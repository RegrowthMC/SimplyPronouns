package me.dave.simplypronouns;

import me.dave.simplypronouns.data.config.ConfigManager;
import me.dave.simplypronouns.data.pronouns.PronounsManager;
import me.dave.simplypronouns.data.user.UserManager;
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
