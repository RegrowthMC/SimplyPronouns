package org.lushplugins.simplypronouns.data.pronouns;

import org.lushplugins.simplypronouns.data.storage.MysqlHandler;
import org.enchantedskies.EnchantedStorage.Storage;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.concurrent.CompletableFuture;

public class PronounsMysqlStorage implements Storage<Pronouns, Integer> {

    @Override
    public CompletableFuture<Pronouns> load(Integer id) {
        CompletableFuture<Pronouns> completableFuture = new CompletableFuture<>();

        MysqlHandler.conn().thenAccept((connection) -> {
            if (connection == null) {
                completableFuture.complete(null);
                return;
            }

            try {
                PreparedStatement statement = connection.prepareStatement(
                    "SELECT * FROM simplypronouns_pronouns WHERE id = ?;"
                );
                statement.setInt(1, id);

                ResultSet resultSet = statement.executeQuery();
                Pronouns pronouns = resultSet.next() ? new Pronouns(id, resultSet.getString("pronouns"), resultSet.getString("customFormat")) : null;
                completableFuture.complete(pronouns);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });

        return completableFuture;
    }

    @Override
    public void save(Pronouns pronouns) {
        MysqlHandler.conn().thenAccept((connection) -> {
            if (connection == null) return;

            try {
                PreparedStatement statement = connection.prepareStatement(
                    "REPLACE INTO simplypronouns_pronouns(id, pronouns, customFormat) VALUES(?, ? ?);"
                );
                statement.setInt(1, pronouns.id());
                statement.setString(2, pronouns.pronouns());
                statement.setString(3, pronouns.customFormat());
                statement.executeUpdate();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
    }
}
