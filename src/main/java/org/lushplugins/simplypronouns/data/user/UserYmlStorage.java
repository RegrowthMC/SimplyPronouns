package org.lushplugins.simplypronouns.data.user;

import org.lushplugins.simplypronouns.SimplyPronouns;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.enchantedskies.EnchantedStorage.Storage;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class UserYmlStorage implements Storage<PronounsUser, UUID> {
    private final File dataFolder = new File(SimplyPronouns.getInstance().getDataFolder(), "data");

    @Override
    public CompletableFuture<PronounsUser> load(UUID uuid) {
        ConfigurationSection configurationSection = loadOrCreateFile(uuid);
        int pronounsId = configurationSection.getInt("pronouns-id", -1);
        return CompletableFuture.completedFuture(new PronounsUser(uuid, pronounsId));
    }

    @Override
    public void save(@NotNull PronounsUser pronounsUser) {
        YamlConfiguration yamlConfiguration = loadOrCreateFile(pronounsUser.getUniqueId());

        Player player = Bukkit.getPlayer(pronounsUser.getUniqueId());
        yamlConfiguration.set("name", player != null ? player.getName() : "Error: Could not get username, will load when the player next joins");
        yamlConfiguration.set("pronouns-id", pronounsUser.getPronounsId());
        File file = new File(dataFolder, pronounsUser.getUniqueId().toString() + ".yml");
        try {
            yamlConfiguration.save(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public YamlConfiguration loadOrCreateFile(@NotNull UUID uuid) {
        File file = new File(dataFolder, uuid + ".yml");
        YamlConfiguration yamlConfiguration = YamlConfiguration.loadConfiguration(file);
        if (yamlConfiguration.getString("name") == null) {
            Player player = Bukkit.getPlayer(uuid);

            String username;
            if (player != null) username = player.getName();
            else username = "Error: Could not get username, will load when the player next joins";
            yamlConfiguration.set("name", username);
            yamlConfiguration.set("pronouns-id", -1);
            try {
                yamlConfiguration.save(file);
            } catch (IOException err) {
                err.printStackTrace();
            }
        }
        return yamlConfiguration;
    }
}
