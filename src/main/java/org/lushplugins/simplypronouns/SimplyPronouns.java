package org.lushplugins.simplypronouns;

import org.lushplugins.lushlib.LushLib;
import org.lushplugins.lushlib.plugin.SpigotPlugin;
import org.lushplugins.simplypronouns.commands.SimplyPronounsCommand;
import org.lushplugins.simplypronouns.commands.preferredname.CheckPreferredNameCommand;
import org.lushplugins.simplypronouns.commands.preferredname.PreferredNameCommand;
import org.lushplugins.simplypronouns.commands.pronouns.CheckPronounsCommand;
import org.lushplugins.simplypronouns.commands.pronouns.PronounsCommand;
import org.lushplugins.simplypronouns.config.ConfigManager;
import org.lushplugins.simplypronouns.data.UserManager;
import org.lushplugins.simplypronouns.hooks.PlaceholderAPIHook;
import org.lushplugins.simplypronouns.hooks.filter.FilterHook;
import org.lushplugins.simplypronouns.hooks.filter.LushChatFilterHook;
import org.lushplugins.simplypronouns.listener.PlayerListener;
import org.lushplugins.simplypronouns.pronouns.PronounManager;
import org.lushplugins.simplypronouns.storage.StorageManager;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public final class SimplyPronouns extends SpigotPlugin {
    private static SimplyPronouns plugin;

    private ConfigManager configManager;
    private StorageManager storageManager;
    private PronounManager pronounManager;
    private UserManager userManager;
    private List<FilterHook> filterHooks;

    @Override
    public void onLoad() {
        plugin = this;
        LushLib.getInstance().enable(plugin);
    }

    @Override
    public void onEnable() {
        configManager = new ConfigManager();
        configManager.reloadConfig();
        pronounManager = new PronounManager();
        storageManager = new StorageManager();
        userManager = new UserManager();

        filterHooks = new ArrayList<>();
        addHook("LushChatFilter", () -> filterHooks.add(new LushChatFilterHook()));
        addHook("PlaceholderAPI", () -> new PlaceholderAPIHook().enable());

        registerListener(new PlayerListener());

        registerCommand(new SimplyPronounsCommand());
        registerCommand(new PronounsCommand());
        registerCommand(new CheckPronounsCommand());
        registerCommand(new PreferredNameCommand());
        registerCommand(new CheckPreferredNameCommand());
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

    public boolean isBlockedByFilters(String string) {
        for (FilterHook hook : filterHooks) {
            if (!Objects.equals(hook.filter(string), string)) {
                return true;
            }
        }

        return false;
    }

    public static SimplyPronouns getInstance() {
        return plugin;
    }
}
