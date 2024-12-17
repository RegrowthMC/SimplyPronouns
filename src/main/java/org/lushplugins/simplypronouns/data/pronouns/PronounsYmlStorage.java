package org.lushplugins.simplypronouns.data.pronouns;

import org.lushplugins.simplypronouns.SimplyPronouns;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.enchantedskies.EnchantedStorage.Storage;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.CompletableFuture;

public class PronounsYmlStorage implements Storage<Pronouns, Integer> {
    private final File dataFolder = new File(SimplyPronouns.getInstance().getDataFolder(), "pronouns.yml");

    @Override
    public CompletableFuture<Pronouns> load(Integer id) {
        ConfigurationSection configurationSection = loadOrCreateFile();
        String pronouns = configurationSection.getString("pronouns");
        String customFormat = configurationSection.getString("custom-format");
        return CompletableFuture.completedFuture(new Pronouns(id, pronouns, customFormat));
    }

    @Override
    public void save(Pronouns pronouns) {
        YamlConfiguration yamlConfiguration = loadOrCreateFile();

        yamlConfiguration.createSection(String.valueOf(pronouns.id()));
        yamlConfiguration.set("pronouns", pronouns.pronouns());
        yamlConfiguration.set("custom-format", pronouns.customFormat());
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
