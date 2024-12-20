package org.lushplugins.simplypronouns.data;

import org.lushplugins.simplypronouns.SimplyPronouns;

import java.util.HashMap;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class UserManager {
    private final HashMap<UUID, PronounsUser> userCache = new HashMap<>();

    public PronounsUser getCachedUser(UUID uuid) {
        return userCache.get(uuid);
    }

    public CompletableFuture<PronounsUser> getUser(UUID uuid) {
        return getUser(uuid, true);
    }

    public CompletableFuture<PronounsUser> getUser(UUID uuid, boolean cache) {
        PronounsUser user = userCache.get(uuid);
        return user != null ? CompletableFuture.completedFuture(user) : loadUser(uuid, cache);
    }

    public CompletableFuture<PronounsUser> loadUser(UUID uuid) {
        return loadUser(uuid, true);
    }

    public CompletableFuture<PronounsUser> loadUser(UUID uuid, boolean cache) {
        return SimplyPronouns.getInstance().getStorageManager().loadPronouns(uuid).thenCompose(pronouns -> {
            CompletableFuture<PronounsUser> future;
            if (pronouns == null) {
                future = CompletableFuture.completedFuture(new PronounsUser(uuid, null));
            } else {
                future = SimplyPronouns.getInstance().getPronounManager().getPronouns(pronouns.split("/"))
                    .thenApply(individualPronouns -> new PronounsUser(uuid, individualPronouns));
            }

            future.thenAccept(user -> {
                if (cache) {
                    userCache.put(uuid, user);
                }
            });

            return future;
        });
    }

    public void unloadUser(UUID uuid) {
        userCache.remove(uuid);
    }
}
