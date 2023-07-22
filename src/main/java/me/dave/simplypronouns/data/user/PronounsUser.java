package me.dave.simplypronouns.data.user;

import java.util.UUID;

public class PronounsUser {
    private final UUID uuid;
    private final int pronounsId;

    public PronounsUser(UUID uuid, int pronounsId) {
        this.uuid = uuid;
        this.pronounsId = pronounsId;
    }

    public UUID getUniqueId() {
        return uuid;
    }

    public int getPronounsId() {
        return pronounsId;
    }
}
