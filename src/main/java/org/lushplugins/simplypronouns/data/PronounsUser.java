package org.lushplugins.simplypronouns.data;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.lushplugins.simplypronouns.SimplyPronouns;
import org.lushplugins.simplypronouns.pronouns.Pronouns;

import java.util.UUID;

public class PronounsUser {
    private final UUID uuid;
    private Pronouns pronouns;
    private String preferredName;

    public PronounsUser(@NotNull UUID uuid, @Nullable Pronouns pronouns, @Nullable String preferredName) {
        this.uuid = uuid;
        this.pronouns = pronouns;
        this.preferredName = preferredName;
    }

    public @NotNull UUID getUniqueId() {
        return uuid;
    }

    public @Nullable Pronouns getPronouns() {
        return pronouns;
    }

    public void setPronouns(@Nullable Pronouns pronouns) {
        this.pronouns = pronouns;
        SimplyPronouns.getInstance().getStorageManager().savePronounsUser(this);
    }

    public @Nullable String getPreferredName() {
        return preferredName;
    }

    public void setPreferredName(@Nullable String preferredName) {
        this.preferredName = preferredName;
        SimplyPronouns.getInstance().getStorageManager().savePronounsUser(this);
    }
}
