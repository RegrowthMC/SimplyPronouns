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
     * Add a pronoun to cache
     * @param pronoun pronoun to cache
     */
    public void cachePronoun(Pronoun pronoun) {
        this.pronounCache.put(pronoun.getPronoun(), new WeakReference<>(pronoun));
    }

    /**
     * Get a pronoun from cache or load it from storage
     * @param rawPronoun pronoun to get
     * @return future to return collected pronoun
     */
    public CompletableFuture<Pronoun> findPronoun(String rawPronoun) {
        if (rawPronoun.contains("/")) {
            throw new IllegalArgumentException(String.format("'%s' appears to be multiple pronouns, this method only accepts a singular pronoun", rawPronoun));
        }

        return WeakRefMapUtils.get(pronounCache, rawPronoun, () -> {
            //noinspection CodeBlock2Expr
            return SimplyPronouns.getInstance().getStorageManager().loadPronoun(rawPronoun);
        });
    }

    /**
     * Create a pronoun object from a string
     * @param rawPronoun pronoun as string
     * @return compiled pronoun
     */
    public Pronoun createPronoun(String rawPronoun) {
        return createPronoun(rawPronoun, Pronoun.Status.AWAITING);
    }

    /**
     * Create a new pronoun object from a string
     * @param rawPronoun pronoun as string
     * @param status approval status of pronoun
     * @return compiled pronoun
     */
    public Pronoun createPronoun(String rawPronoun, Pronoun.Status status) {
        Pronoun pronoun = new Pronoun(rawPronoun, status);
        pronoun.saveStatus();
        return pronoun;
    }

    /**
     * Get a pronoun from cache, load it from storage or create it
     * @param rawPronoun pronoun to get
     * @return future to return collected pronoun
     */
    public CompletableFuture<Pronoun> findOrCreatePronoun(String rawPronoun) {
        return findPronoun(rawPronoun).thenApply(pronoun -> {
            //noinspection CodeBlock2Expr
            return pronoun != null ? pronoun : createPronoun(rawPronoun, Pronoun.Status.AWAITING);
        });
    }

    public CompletableFuture<Pronouns> compilePronouns(String[] pronouns) {
        // Gather cached pronouns
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

        // If all pronouns are cached then return early
        if (uncachedPronouns.isEmpty()) {
            return CompletableFuture.completedFuture(new Pronouns(cachedPronouns.toArray(Pronoun[]::new)));
        }

        // Load or create any pronouns that haven't been found
        return SimplyPronouns.getInstance().getStorageManager().loadPronouns(uncachedPronouns.toArray(String[]::new)).thenApply((loadedPronouns) -> {
            for (int i = 0; i < loadedPronouns.size(); i++) {
                Pronoun loadedPronoun = loadedPronouns.get(i);
                if (loadedPronoun == null) {
                    loadedPronoun = createPronoun(uncachedPronouns.get(i), Pronoun.Status.AWAITING);
                }

                cachedPronouns.add(loadedPronoun);
                cachePronoun(loadedPronoun);
            }

            return new Pronouns(cachedPronouns.toArray(Pronoun[]::new));
        });
    }
}
