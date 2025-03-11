package org.lushplugins.simplypronouns.storage.type;

import com.mysql.cj.jdbc.MysqlDataSource;
import org.bukkit.configuration.ConfigurationSection;

import javax.sql.DataSource;

public class MySQLStorage extends AbstractSQLStorage {

    @Override
    public void enable(ConfigurationSection config) {
        super.enable(config);
        runSqlFile("storage/mysql_setup.sql");
    }

    @Override
    public String getSavePronounsUserStatement() {
        return String.format("""
            INSERT INTO %s (uuid, username, pronouns, preferred_name)
            VALUES (?, ?, ?, ?)
            ON DUPLICATE KEY UPDATE
            pronouns = VALUES(pronouns),
            preferred_name = VALUES(preferred_name);
            """, USER_TABLE);
    }

    @Override
    public String getSavePronounStatusStatement() {
        return String.format("""
            INSERT INTO %s (pronoun, status)
            VALUES (?, ?)
            ON DUPLICATE KEY UPDATE
            status = VALUES(status);
            """, PRONOUN_TABLE);
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
