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
            //noinspection CodeBlock2Expr
            return SimplyPronouns.getInstance().getPronounManager().getPronouns(pronouns.split("/")).thenApply(individualPronouns -> {
                PronounsUser pronounsUser = new PronounsUser(uuid, individualPronouns);

                if (cache) {
                    userCache.put(uuid, pronounsUser);
                }

                return pronounsUser;
            });
        });
    }

    public void unloadUser(UUID uuid) {
        userCache.remove(uuid);
    }
}
