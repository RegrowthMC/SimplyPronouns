package org.lushplugins.simplypronouns.storage;

import org.bukkit.configuration.ConfigurationSection;
import org.lushplugins.simplypronouns.pronouns.Pronoun;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

/*
 * Pronoun Table: pronoun | status
 * User Table: uuid | name | pronouns
 */
public interface Storage {

    default void enable(ConfigurationSection config) {}

    default void disable() {}

    String loadPronouns(UUID uuid);

    void savePronouns(UUID uuid, String pronouns);

    default Pronoun loadPronoun(String pronoun) {
        Pronoun.Status status = loadPronounStatus(pronoun);
        return new Pronoun(pronoun, status);
    }

    default List<Pronoun> loadPronouns(String[] pronouns) {
        return Arrays.stream(pronouns)
            .map(this::loadPronoun)
            .toList();
    }

    // TODO: Add a way of loading sections for pages (currently just limit to loading 100)
    List<String> loadPronouns(Pronoun.Status status);

    Pronoun.Status loadPronounStatus(String pronoun);

    void savePronounStatus(String pronoun, Pronoun.Status status);

    List<String> searchForUser(String query);
}
