package me.dave.simplypronouns.data.pronouns;

import me.dave.simplypronouns.data.storage.MysqlHandler;
import org.enchantedskies.EnchantedStorage.IOHandler;
import org.enchantedskies.EnchantedStorage.Storage;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class PronounsIOHandler extends IOHandler<Pronouns, Integer> {

    public PronounsIOHandler(Storage<Pronouns, Integer> storage) {
        super(storage);
    }

    public CompletableFuture<List<Pronouns>> loadAllData() {
        CompletableFuture<List<Pronouns>> completableFuture = new CompletableFuture<>();

        MysqlHandler.conn().thenAccept(connection -> {
            try {
                PreparedStatement statement = connection.prepareStatement(
                    "SELECT * FROM simplypronouns_pronouns;"
                );

                ResultSet resultSet = statement.executeQuery();

                List<Pronouns> pronouns = new ArrayList<>();
                while (resultSet.next()) {
                    pronouns.add(new Pronouns(resultSet.getInt("id"), resultSet.getString("pronouns"), resultSet.getString("customFormat")));
                }
                completableFuture.complete(pronouns);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });

        return completableFuture;
    }
}
