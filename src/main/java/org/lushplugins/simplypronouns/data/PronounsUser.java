package org.lushplugins.simplypronouns.data;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.lushplugins.simplypronouns.SimplyPronouns;
import org.lushplugins.simplypronouns.pronouns.Pronouns;

import java.util.UUID;

public class PronounsUser {
    private final UUID uuid;
    private Pronouns pronouns;

    public PronounsUser(@NotNull UUID uuid, @Nullable Pronouns pronouns) {
        this.uuid = uuid;
        this.pronouns = pronouns;
    }

    public @NotNull UUID getUniqueId() {
        return uuid;
    }

    public @Nullable Pronouns getPronouns() {
        return pronouns;
    }

    public void setPronouns(@Nullable Pronouns pronouns) {
        this.pronouns = pronouns;

        if (pronouns != null) {
            SimplyPronouns.getInstance().getStorageManager().savePronounsUser(uuid, null, pronouns.asString());
        }
    }
}
