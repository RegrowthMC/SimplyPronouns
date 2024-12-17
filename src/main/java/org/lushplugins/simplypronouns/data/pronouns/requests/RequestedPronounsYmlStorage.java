package org.lushplugins.simplypronouns.data.pronouns.requests;

import org.lushplugins.simplypronouns.SimplyPronouns;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.enchantedskies.EnchantedStorage.Storage;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.CompletableFuture;

public class RequestedPronounsYmlStorage implements Storage<RequestedPronouns, String> {

    private final File dataFolder = new File(SimplyPronouns.getInstance().getDataFolder(), "pronouns.yml");

    @Override
    public CompletableFuture<RequestedPronouns> load(String pronouns) {
        ConfigurationSection configurationSection = loadOrCreateFile();
        boolean denied = configurationSection.getBoolean("denied");
        return CompletableFuture.completedFuture(new RequestedPronouns(pronouns, denied));
    }

    @Override
    public void save(RequestedPronouns requestedPronouns) {
        YamlConfiguration yamlConfiguration = loadOrCreateFile();

        yamlConfiguration.createSection(String.valueOf(requestedPronouns.pronouns()));
        yamlConfiguration.set("denied", requestedPronouns.denied());
        try {
            yamlConfiguration.save(dataFolder);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public YamlConfiguration loadOrCreateFile() {
        File file = new File(dataFolder, "pronouns.yml");
        return YamlConfiguration.loadConfiguration(file);
    }
}
