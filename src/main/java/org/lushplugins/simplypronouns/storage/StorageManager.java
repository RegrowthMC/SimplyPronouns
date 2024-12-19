package org.lushplugins.simplypronouns.storage;

import org.bukkit.configuration.file.FileConfiguration;
import org.lushplugins.simplypronouns.SimplyPronouns;
import org.lushplugins.simplypronouns.pronouns.Pronoun;
import org.lushplugins.simplypronouns.storage.type.MySQLStorage;
import org.lushplugins.simplypronouns.storage.type.SQLiteStorage;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@SuppressWarnings("UnusedReturnValue")
public class StorageManager {
    private final ExecutorService threads = Executors.newFixedThreadPool(1);
    private Storage storage;

    public StorageManager() {
        SimplyPronouns.getInstance().saveDefaultResource("storage.yml");
        reload();
    }

    public void reload() {
        disable();

        FileConfiguration config = SimplyPronouns.getInstance().getConfigResource("storage.yml");
        String storageType = config.getString("type");
        if (storageType == null) {
            SimplyPronouns.getInstance().getLogger().severe("No storage type is defined");
            return;
        }

        switch (storageType) {
            case "mysql", "mariadb" -> storage = new MySQLStorage();
            case "sqlite" -> storage = new SQLiteStorage();
            default -> {
                SimplyPronouns.getInstance().getLogger().severe(String.format("'%s' is not a valid storage type", storageType));
                return;
            }
        }

        runAsync(() -> storage.enable(config));
    }

    public void disable() {
        if (storage != null) {
            runAsync(storage::disable);
        }
    }

    public CompletableFuture<String> loadPronouns(UUID uuid) {
        return runAsync(() -> storage.loadPronouns(uuid));
    }

    public CompletableFuture<Void> savePronouns(UUID uuid, String pronouns) {
        return runAsync(() -> storage.savePronouns(uuid, pronouns));
    }

    public CompletableFuture<Pronoun> loadPronoun(String pronoun) {
        return runAsync(() -> storage.loadPronoun(pronoun));
    }

    /**
     * Load an array of pronouns
     * @param pronouns array of pronouns to be loaded
     * @return list of loaded pronouns, if a pronoun could not be found the index will contain a {@code null} object
     */
    public CompletableFuture<List<Pronoun>> loadPronouns(String[] pronouns) {
        return runAsync(() -> storage.loadPronouns(pronouns));
    }

    public CompletableFuture<Pronoun.Status> loadPronounStatus(String pronoun) {
        return runAsync(() -> storage.loadPronounStatus(pronoun));
    }

    public CompletableFuture<Void> savePronounStatus(String pronoun, Pronoun.Status status) {
        return runAsync(() -> storage.savePronounStatus(pronoun, status));
    }

    public CompletableFuture<List<String>> searchForUser(String query) {
        return runAsync(() -> storage.searchForUser(query));
    }

    private <T> CompletableFuture<T> runAsync(Callable<T> callable) {
        CompletableFuture<T> future = new CompletableFuture<>();
        threads.submit(() -> {
            try {
                future.complete(callable.call());
            } catch (Throwable e) {
                e.printStackTrace();
                future.completeExceptionally(e);
            }
        });
        return future;
    }

    private CompletableFuture<Void> runAsync(Runnable runnable) {
        CompletableFuture<Void> future = new CompletableFuture<>();
        threads.submit(() -> {
            try {
                runnable.run();
                future.complete(null);
            } catch (Throwable e) {
                e.printStackTrace();
                future.completeExceptionally(e);
            }
        });
        return future;
    }
}
