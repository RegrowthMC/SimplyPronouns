package org.lushplugins.simplypronouns.data.user;

import org.lushplugins.simplypronouns.data.storage.MysqlHandler;
import org.enchantedskies.EnchantedStorage.Storage;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class UserMysqlStorage implements Storage<PronounsUser, UUID> {

    @Override
    public CompletableFuture<PronounsUser> load(UUID uuid) {
        CompletableFuture<PronounsUser> completableFuture = new CompletableFuture<>();

        MysqlHandler.conn().thenAccept((connection) -> {
            if (connection == null) {
                completableFuture.complete(null);
                return;
            }

            try {
                PreparedStatement statement = connection.prepareStatement(
                    "SELECT * FROM simplypronouns_users WHERE uuid = ?;"
                );
                statement.setString(1, uuid.toString());

                ResultSet resultSet = statement.executeQuery();
                PronounsUser pronounsUser;
                if (resultSet.next()) {
                    pronounsUser = new PronounsUser(uuid, resultSet.getInt("pronounsId"));
                } else {
                    pronounsUser = new PronounsUser(uuid, -1);
                    save(pronounsUser);
                }
                completableFuture.complete(pronounsUser);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });

        return completableFuture;
    }

    @Override
    public void save(PronounsUser pronounsUser) {
        MysqlHandler.conn().thenAccept((connection) -> {
            if (connection == null) return;

            try {
                PreparedStatement statement = connection.prepareStatement(
                    "REPLACE INTO simplypronouns_users(uuid, pronounsId) VALUES(?, ?);"
                );
                statement.setString(1, pronounsUser.getUniqueId().toString());
                statement.setInt(2, pronounsUser.getPronounsId());
                statement.executeUpdate();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
    }
}
