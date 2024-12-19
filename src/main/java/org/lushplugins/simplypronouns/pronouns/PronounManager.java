package org.lushplugins.simplypronouns.pronouns;

import org.jetbrains.annotations.Nullable;
import org.lushplugins.simplypronouns.SimplyPronouns;
import org.lushplugins.simplypronouns.util.WeakRefMapUtils;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class PronounManager {
    private final HashMap<String, WeakReference<Pronoun>> pronounCache = new HashMap<>();

    /**
     * Get a pronoun from cache
     * @param pronoun pronoun to get
     * @return the cached pronoun or {@code null} if the pronoun was not found
     */
    public @Nullable Pronoun getCachedPronoun(String pronoun) {
        return WeakRefMapUtils.get(pronounCache, pronoun);
    }

    /**
     * Get a pronoun from cache or load it from storage
     * @param rawPronoun pronoun to get
     * @return future to return collected pronoun, if the pronoun isn't found it'll be created
     */
    public CompletableFuture<Pronoun> getOrCreatePronoun(String rawPronoun) {
        if (rawPronoun.contains("/")) {
            throw new IllegalArgumentException(String.format("'%s' appears to be multiple pronouns, this class only accepts a singular pronoun", rawPronoun));
        }

        return WeakRefMapUtils.get(pronounCache, rawPronoun, () -> {
            //noinspection CodeBlock2Expr
            return SimplyPronouns.getInstance().getStorageManager().loadPronoun(rawPronoun).thenApply(pronoun -> {
                //noinspection CodeBlock2Expr
                return pronoun != null ? pronoun : new Pronoun(rawPronoun, Pronoun.Status.AWAITING);
            });
        });
    }

    /**
     * Get pronouns from cache or load it from storage
     * @param pronouns pronouns to get
     * @return future to return collected pronouns, if a pronoun isn't found it'll be created
     */
    public CompletableFuture<Pronouns> getPronouns(String[] pronouns) {
        List<Pronoun> cachedPronouns = new ArrayList<>();
        List<String> uncachedPronouns = new ArrayList<>();
        for (String pronoun : pronouns) {
            Pronoun cachedPronoun = getCachedPronoun(pronoun);
            if (cachedPronoun != null) {
                cachedPronouns.add(cachedPronoun);
            } else {
                uncachedPronouns.add(pronoun);
            }
        }

        if (uncachedPronouns.isEmpty()) {
            return CompletableFuture.completedFuture(new Pronouns(cachedPronouns.toArray(Pronoun[]::new)));
        }

        return SimplyPronouns.getInstance().getStorageManager().loadPronouns(uncachedPronouns.toArray(String[]::new)).thenApply((loadedPronouns) -> {
            for (int i = 0; i < loadedPronouns.size(); i++) {
                Pronoun loadedPronoun = loadedPronouns.get(i);
                if (loadedPronoun != null) {
                    cachedPronouns.add(loadedPronoun);
                } else {
                    cachedPronouns.add(new Pronoun(uncachedPronouns.get(i), Pronoun.Status.AWAITING));
                }
            }

            return new Pronouns(cachedPronouns.toArray(Pronoun[]::new));
        });
    }
}
