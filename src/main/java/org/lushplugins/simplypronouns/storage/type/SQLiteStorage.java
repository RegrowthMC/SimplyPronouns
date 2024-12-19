package org.lushplugins.simplypronouns.storage.type;

import org.bukkit.configuration.ConfigurationSection;
import org.lushplugins.simplypronouns.SimplyPronouns;

import javax.sql.DataSource;
import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.logging.Level;

public class SQLiteStorage extends MySQLStorage {
    private static final String DATABASE_PATH = new File(SimplyPronouns.getInstance().getDataFolder(), "data.db").getAbsolutePath();

    @Override
    public String getSavePronounsStatement() {
        return String.format("INSERT INTO %s (uuid, username, pronouns) VALUES (?, ?, ?) ON CONFLICT (uuid) DO UPDATE SET pronouns = EXCLUDED.pronouns;", USER_TABLE);
    }

    @Override
    public String getSavePronounStatusStatement() {
        return String.format("INSERT INTO %s (pronoun, status) VALUES (?, ?) ON CONFLICT (pronoun) DO UPDATE SET status = EXCLUDED.status;", PRONOUN_TABLE);
    }

    @Override
    protected Connection conn() {
        try {
            return DriverManager.getConnection("jdbc:sqlite:" + DATABASE_PATH);
        } catch (SQLException e) {
            SimplyPronouns.getInstance().log(Level.SEVERE, "An error occurred whilst getting a connection: ", e);
            return null;
        }
    }

    @Override
    protected DataSource setupDataSource(ConfigurationSection config) {
        return null;
    }
}
