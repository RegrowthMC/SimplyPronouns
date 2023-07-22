package me.dave.simplypronouns.data.user;

import me.dave.simplypronouns.SimplyPronouns;
import org.bukkit.entity.Player;
import org.enchantedskies.EnchantedStorage.IOHandler;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class UserManager {
    private IOHandler<PronounsUser, UUID> ioHandler;
    private final HashMap<UUID, PronounsUser> uuidToPronounsUser = new HashMap<>();

    public UserManager() {
        reload();
    }

    public void reload() {
        if (ioHandler != null) ioHandler.disableIOHandler();

        String storageType = SimplyPronouns.getConfigManager().getDatabaseType();
        switch (storageType.toLowerCase()) {
            case "yml" -> ioHandler = new IOHandler<>(new UserYmlStorage());
            case "mysql" -> ioHandler = new IOHandler<>(new UserMysqlStorage());
        }
    }

    @NotNull
    public PronounsUser getPronounsUser(@NotNull Player player) {
        UUID uuid = player.getUniqueId();

        PronounsUser pronounsUser = uuidToPronounsUser.get(uuid);
        if (pronounsUser == null) {
            pronounsUser = new PronounsUser(uuid, -1);
            uuidToPronounsUser.put(uuid, pronounsUser);
        }
        return pronounsUser;
    }

    public CompletableFuture<PronounsUser> loadPronounsUser(UUID uuid) {
        return ioHandler.loadData(uuid).thenApply(pronounsUser -> {
            uuidToPronounsUser.put(uuid, pronounsUser);
            return pronounsUser;
        });
    }

    public void unloadPronounsUser(UUID uuid) {
        uuidToPronounsUser.remove(uuid);
    }

    public void savePronounsUser(PronounsUser pronounsUser) {
        ioHandler.saveData(pronounsUser);
    }

    public IOHandler<PronounsUser, UUID> getIoHandler() {
        return ioHandler;
    }
}
