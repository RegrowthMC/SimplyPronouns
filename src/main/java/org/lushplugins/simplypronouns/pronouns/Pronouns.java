package org.lushplugins.simplypronouns.pronouns;

import java.util.Arrays;

public class Pronouns {
    private final Pronoun[] pronouns;

    public Pronouns(Pronoun[] pronouns) {
        this.pronouns = pronouns;
    }

    public Pronoun[] getIndividualPronouns() {
        return pronouns;
    }

    public boolean areAllApproved() {
        return Arrays.stream(pronouns).allMatch(pronoun -> pronoun.getStatus() == Pronoun.Status.APPROVED);
    }

    public boolean areAnyDenied() {
        return Arrays.stream(pronouns).anyMatch(pronoun -> pronoun.getStatus() == Pronoun.Status.DENIED);
    }

    public String asString() {
        String[] pronouns = Arrays.stream(this.pronouns)
            .map(Pronoun::getPronoun)
            .toArray(String[]::new);

        return String.join("/", pronouns);
    }
}