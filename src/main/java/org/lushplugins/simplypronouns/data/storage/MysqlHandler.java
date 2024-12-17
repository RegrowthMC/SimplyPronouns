package org.lushplugins.simplypronouns.data.storage;

import com.mysql.cj.jdbc.MysqlConnectionPoolDataSource;
import com.mysql.cj.jdbc.MysqlDataSource;
import me.dave.chatcolorhandler.ChatColorHandler;
import org.lushplugins.simplypronouns.SimplyPronouns;
import org.lushplugins.simplypronouns.data.user.UserManager;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.scheduler.BukkitRunnable;

import javax.sql.DataSource;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.concurrent.CompletableFuture;
import java.util.logging.Level;
import java.util.stream.Collectors;

public class MysqlHandler {
    private static final int MAX_CONNECT_ATTEMPTS = 5;
    private static MysqlDataSource dataSource = null;

    public static CompletableFuture<Connection> conn() {
        CompletableFuture<Connection> completableFuture = new CompletableFuture<>();

        getOrInitialiseConnection().thenAccept((dataSource) -> {
            try {
                completableFuture.complete(dataSource.getConnection());
            } catch (SQLException throwable) {
                throwable.printStackTrace();
                completableFuture.complete(null);
            }
        });

        return completableFuture;
    }

    public static void setupDatabase() {
        getOrInitialiseConnection().thenAccept((dataSource) -> {
            String setup;
            try (InputStream in = UserManager.class.getClassLoader().getResourceAsStream("dbsetup.sql")) {
                setup = new BufferedReader(new InputStreamReader(in)).lines().collect(Collectors.joining("\n"));
            } catch (IOException e) {
                SimplyPronouns.getInstance().getLogger().log(Level.SEVERE, "Could not read db setup file.", e);
                e.printStackTrace();
                return;
            }
            String[] queries = setup.split(";");
            for (String query : queries) {
                if (query.isEmpty()) continue;


                try (Connection conn = dataSource.getConnection(); PreparedStatement stmt = conn.prepareStatement(query)) {
                    stmt.execute();
                } catch (SQLException e) {
                    e.printStackTrace();
                    return;
                }
            }
            SimplyPronouns.getInstance().getLogger().info(ChatColorHandler.translateAlternateColorCodes("&2Database setup complete."));
        });
    }

    public static CompletableFuture<MysqlDataSource> getOrInitialiseConnection() {
        CompletableFuture<MysqlDataSource> completableFuture = new CompletableFuture<>();

        if (dataSource != null) {
            completableFuture.complete(dataSource);
        }
        else {
            ConfigurationSection databaseSection = SimplyPronouns.getConfigManager().getDatabaseData();

            MysqlDataSource dataSource = new MysqlConnectionPoolDataSource();
            dataSource.setDatabaseName(databaseSection.getString("name"));
            dataSource.setServerName(databaseSection.getString("host"));
            dataSource.setPortNumber(databaseSection.getInt("port"));
            dataSource.setUser(databaseSection.getString("user"));
            dataSource.setPassword(databaseSection.getString("password", ""));

            testConnection(dataSource).thenAccept((success) -> {
                if (!success) SimplyPronouns.getInstance().getLogger().severe("Failed to establish a database connection");
                MysqlHandler.dataSource = dataSource;
                setupDatabase();
                completableFuture.complete(dataSource);
            });
        }

        return completableFuture;
    }

    private static CompletableFuture<Boolean> testConnection(DataSource dataSource) {
        CompletableFuture<Boolean> completableFuture = new CompletableFuture<>();

        final int[] connectionAttempt = {0};
        new BukkitRunnable() {
            @Override
            public void run() {
                connectionAttempt[0]++;

                boolean success = false;
                try (Connection conn = dataSource.getConnection()) {
                    if (!conn.isValid(1000)) {
                        throw new SQLException("Could not establish database connection.");
                    }
                    success = true;
                } catch (SQLException e) {
                    e.printStackTrace();
                }

                if (success) {
                    cancel();
                    completableFuture.complete(true);
                }
                else if (connectionAttempt[0] >= MAX_CONNECT_ATTEMPTS) {
                    cancel();
                    completableFuture.complete(false);
                }
            }
        }.runTaskTimer(SimplyPronouns.getInstance(), 0, 100);

        return completableFuture;
    }
}
