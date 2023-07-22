package me.dave.simplypronouns.data.pronouns.requests;

import me.dave.simplypronouns.data.storage.MysqlHandler;
import org.enchantedskies.EnchantedStorage.Storage;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.concurrent.CompletableFuture;

public class RequestedPronounsMysqlStorage implements Storage<RequestedPronouns, String> {

    @Override
    public CompletableFuture<RequestedPronouns> load(String pronouns) {
        CompletableFuture<RequestedPronouns> completableFuture = new CompletableFuture<>();

        MysqlHandler.conn().thenAccept((connection) -> {
            if (connection == null) {
                completableFuture.complete(null);
                return;
            }

            try {
                PreparedStatement statement = connection.prepareStatement(
                    "SELECT * FROM simplypronouns_requests WHERE pronouns = ?;"
                );
                statement.setString(1, pronouns);

                ResultSet resultSet = statement.executeQuery();
                RequestedPronouns requestedPronouns = resultSet.next() ? new RequestedPronouns(resultSet.getString("pronouns"), resultSet.getBoolean("denied")) : null;
                completableFuture.complete(requestedPronouns);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });

        return completableFuture;
    }

    @Override
    public void save(RequestedPronouns requestedPronouns) {
        MysqlHandler.conn().thenAccept((connection) -> {
            if (connection == null) return;

            try {
                PreparedStatement statement = connection.prepareStatement(
                    "REPLACE INTO simplypronouns_requests(pronouns, denied) VALUES(?, ?);"
                );
                statement.setString(1, requestedPronouns.pronouns());
                statement.setBoolean(2, requestedPronouns.denied());
                statement.executeUpdate();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
    }
}
