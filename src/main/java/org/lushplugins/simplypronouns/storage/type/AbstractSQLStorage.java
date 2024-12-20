package org.lushplugins.simplypronouns.storage.type;

import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.lushplugins.simplypronouns.SimplyPronouns;
import org.lushplugins.simplypronouns.pronouns.Pronoun;
import org.lushplugins.simplypronouns.storage.Storage;

import javax.sql.DataSource;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;
import java.util.stream.Collectors;

public abstract class AbstractSQLStorage implements Storage {
    protected static final String USER_TABLE = "simplypronouns_users";
    protected static final String PRONOUN_TABLE = "simplypronouns_pronouns";

    private DataSource dataSource;

    @Override
    public void enable(ConfigurationSection config) {
        this.dataSource = setupDataSource(config);
        testDataSourceConnection();
    }

    @Override
    public String loadPronouns(UUID uuid) {
        try (Connection conn = conn();
             PreparedStatement stmt = conn.prepareStatement(String.format("""
                 SELECT *
                 FROM %s
                 WHERE uuid = ?;
                 """, USER_TABLE))
        ) {
            stmt.setString(1, uuid.toString());

            ResultSet results = stmt.executeQuery();
            return results.next() ? results.getString("pronouns") : null;
        } catch (SQLException e) {
            SimplyPronouns.getInstance().getLogger().log(Level.SEVERE, "Failed to load user's pronouns: ", e);
        }

        return null;
    }

    public abstract String getSavePronounsStatement();

    @SuppressWarnings("SqlSourceToSinkFlow")
    @Override
    public void savePronounsUser(@NotNull UUID uuid, @Nullable String username, @Nullable String pronouns) {
        try (Connection conn = conn();
             PreparedStatement stmt = conn.prepareStatement(getSavePronounsStatement())
        ) {
            stmt.setString(1, uuid.toString());
            stmt.setString(2, username);
            stmt.setString(3, pronouns);

            stmt.execute();
        } catch (SQLException e) {
            SimplyPronouns.getInstance().getLogger().log(Level.SEVERE, "Failed to save user's pronouns: ", e);
        }
    }

    @Override
    public List<String> loadPronouns(Pronoun.Status status) {
        try (Connection conn = conn();
             PreparedStatement stmt = conn.prepareStatement(String.format("""
                 SELECT pronoun
                 FROM %s
                 WHERE status = ?
                 LIMIT 100;
                 """, PRONOUN_TABLE))
        ) {
            stmt.setString(1, status.name().toLowerCase());

            List<String> pronouns = new ArrayList<>();
            ResultSet results = stmt.executeQuery();
            while (results.next()) {
                pronouns.add(results.getString("pronoun"));
            }

            return pronouns;
        } catch (SQLException e) {
            SimplyPronouns.getInstance().getLogger().log(Level.SEVERE, "Failed to load user names: ", e);
        }

        return null;
    }

    @Override
    public Pronoun.Status loadPronounStatus(String pronoun) {
        try (Connection conn = conn();
             PreparedStatement stmt = conn.prepareStatement(String.format("""
                 SELECT *
                 FROM %s
                 WHERE pronoun = ?;
                 """, PRONOUN_TABLE))
        ) {
            stmt.setString(1, pronoun);

            ResultSet results = stmt.executeQuery();
            if (results.next()) {
                String status = results.getString("status");

                try {
                    Pronoun.Status.valueOf(status.toUpperCase());
                } catch (IllegalArgumentException e) {
                    SimplyPronouns.getInstance().getLogger().log(Level.SEVERE, String.format("Found invalid status for pronoun '%s': ", pronoun), e);
                    return Pronoun.Status.AWAITING;
                }
            }
        } catch (SQLException e) {
            SimplyPronouns.getInstance().getLogger().log(Level.SEVERE, "Failed to load user's pronouns: ", e);
        }

        return null;
    }

    public abstract String getSavePronounStatusStatement();

    @SuppressWarnings("SqlSourceToSinkFlow")
    @Override
    public void savePronounStatus(String pronoun, Pronoun.Status status) {
        try (Connection conn = conn();
             PreparedStatement stmt = conn.prepareStatement(getSavePronounStatusStatement())
        ) {
            stmt.setString(1, pronoun);
            stmt.setString(2, status.name().toLowerCase());

            stmt.execute();
        } catch (SQLException e) {
            SimplyPronouns.getInstance().getLogger().log(Level.SEVERE, "Failed to save user's pronouns: ", e);
        }
    }

    @Override
    public List<String> searchForUser(String query) {
        try (Connection conn = conn();
             PreparedStatement stmt = conn.prepareStatement(String.format("""
                 SELECT username
                 FROM %s
                 WHERE username LIKE '?'
                 LIMIT 50;
                 """, USER_TABLE))
        ) {
            stmt.setString(1, query);

            List<String> users = new ArrayList<>();
            ResultSet results = stmt.executeQuery();
            while (results.next()) {
                users.add(results.getString("username"));
            }

            return users;
        } catch (SQLException e) {
            SimplyPronouns.getInstance().getLogger().log(Level.SEVERE, "Failed to load user names: ", e);
        }

        // We return an empty list here because this is only used for tab-completion
        return Collections.emptyList();
    }

    protected Connection conn() {
        try {
            return getDataSource().getConnection();
        } catch (SQLException e) {
            SimplyPronouns.getInstance().log(Level.SEVERE, "An error occurred whilst getting a connection: ", e);
            return null;
        }
    }

    protected DataSource getDataSource() {
        return dataSource;
    }

    protected abstract DataSource setupDataSource(ConfigurationSection config);

    protected void runSqlFile(String filePath) {
        String setup;
        try (InputStream in = AbstractSQLStorage.class.getClassLoader().getResourceAsStream(filePath)) {
            setup = new BufferedReader(new InputStreamReader(in)).lines().collect(Collectors.joining(""));
        } catch (IOException e) {
            SimplyPronouns.getInstance().getLogger().log(Level.SEVERE, "Could not read db setup file.", e);
            e.printStackTrace();
            return;
        }

        String[] statements = setup.split("\\|");
        for (String statement : statements) {
            try (Connection conn = conn(); PreparedStatement stmt = conn.prepareStatement(statement)) {
                stmt.execute();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    protected void testDataSourceConnection() {
        try (Connection conn = conn()) {
            if (!conn.isValid(1000)) {
                throw new SQLException("Could not establish database connection.");
            }
        } catch (SQLException e) {
            SimplyPronouns.getInstance().log(Level.SEVERE, "An error occurred whilst testing the data source ", e);
        }
    }
}
