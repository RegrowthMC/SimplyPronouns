package org.lushplugins.simplypronouns;

import org.lushplugins.lushlib.plugin.SpigotPlugin;
import org.lushplugins.simplypronouns.commands.CheckPronounsCommand;
import org.lushplugins.simplypronouns.commands.PronounsCommand;
import org.lushplugins.simplypronouns.config.ConfigManager;
import org.lushplugins.simplypronouns.data.UserManager;
import org.lushplugins.simplypronouns.hooks.PlaceholderAPIHook;
import org.lushplugins.simplypronouns.listener.PlayerListener;
import org.lushplugins.simplypronouns.pronouns.PronounManager;
import org.lushplugins.simplypronouns.storage.StorageManager;

public final class SimplyPronouns extends SpigotPlugin {
    private static SimplyPronouns plugin;

    private ConfigManager configManager;
    private StorageManager storageManager;
    private PronounManager pronounManager;
    private UserManager userManager;

    @Override
    public void onLoad() {
        plugin = this;
    }

    @Override
    public void onEnable() {
        configManager = new ConfigManager();
        configManager.reloadConfig();
        pronounManager = new PronounManager();
        storageManager = new StorageManager();
        userManager = new UserManager();

        addHook("PlaceholderAPI", () -> new PlaceholderAPIHook().enable());

        registerListener(new PlayerListener());

        registerCommand(new CheckPronounsCommand());
        registerCommand(new PronounsCommand());
    }

    @Override
    public void onDisable() {
        // Plugin disable logic
    }

    public ConfigManager getConfigManager() {
        return configManager;
    }

    public PronounManager getPronounManager() {
        return pronounManager;
    }

    public StorageManager getStorageManager() {
        return storageManager;
    }

    public UserManager getUserManager() {
        return userManager;
    }

    public static SimplyPronouns getInstance() {
        return plugin;
    }
}
