package org.lushplugins.simplypronouns.storage.type;

import com.mysql.cj.jdbc.MysqlDataSource;
import org.bukkit.configuration.ConfigurationSection;

import javax.sql.DataSource;

public class MySQLStorage extends AbstractSQLStorage {

    @Override
    public String getSavePronounsStatement() {
        return String.format("INSERT INTO %s (uuid, username, pronouns) VALUES (?, ?, ?) ON DUPLICATE KEY UPDATE pronouns = VALUES(pronouns);", USER_TABLE);
    }

    @Override
    public String getSavePronounStatusStatement() {
        return String.format("INSERT INTO %s (pronoun, status) VALUES (?, ?) ON DUPLICATE KEY UPDATE status = VALUES(status);", PRONOUN_TABLE);
    }

    @Override
    protected DataSource setupDataSource(ConfigurationSection config) {
        MysqlDataSource dataSource = new MysqlDataSource();
        dataSource.setServerName(config.getString("host"));
        dataSource.setPortNumber(config.getInt("port"));
        dataSource.setDatabaseName(config.getString("database"));
        dataSource.setUser(config.getString("username"));
        dataSource.setPassword(config.getString("password"));

        return dataSource;
    }
}
