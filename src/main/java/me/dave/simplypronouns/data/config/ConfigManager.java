package me.dave.simplypronouns.data.config;

import me.dave.simplypronouns.SimplyPronouns;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;

public class ConfigManager {
    private DatabaseConfig databaseConfig;

    public ConfigManager() {
        reloadConfig();
    }

    public void reloadConfig() {
        SimplyPronouns.getInstance().reloadConfig();
        FileConfiguration config = SimplyPronouns.getInstance().getConfig();

        databaseConfig = new DatabaseConfig(config.getString("database.type"), config.getConfigurationSection("database"));
    }

    public String getDatabaseType() {
        return databaseConfig.type;
    }

    public ConfigurationSection getDatabaseData() {
        return databaseConfig.section;
    }

    public record DatabaseConfig(String type, ConfigurationSection section) {}
}
