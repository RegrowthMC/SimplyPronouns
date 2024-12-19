package org.lushplugins.simplypronouns.pronouns;

import org.jetbrains.annotations.ApiStatus;
import org.lushplugins.simplypronouns.SimplyPronouns;

public class Pronoun {
    private final String pronoun;
    private Status status;

    @ApiStatus.Internal
    public Pronoun(String pronoun, Status status) {
        if (pronoun.contains("/")) {
            throw new IllegalArgumentException(String.format("'%s' appears to be multiple pronouns, this class only accepts a singular pronoun", pronoun));
        }

        this.pronoun = pronoun;
        this.status = status;
    }

    public String getPronoun() {
        return pronoun;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        setStatus(status, true);
    }

    public void setStatus(Status status, boolean save) {
        this.status = status;

        if (save) {
            SimplyPronouns.getInstance().getStorageManager().savePronounStatus(pronoun, status);
        }
    }

    public enum Status {
        AWAITING,
        APPROVED,
        DENIED
    }
}
