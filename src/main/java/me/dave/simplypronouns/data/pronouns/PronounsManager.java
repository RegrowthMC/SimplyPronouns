package me.dave.simplypronouns.data.pronouns;

import me.dave.simplypronouns.SimplyPronouns;
import me.dave.simplypronouns.data.pronouns.requests.RequestedPronouns;
import me.dave.simplypronouns.data.pronouns.requests.RequestedPronounsMysqlStorage;
import me.dave.simplypronouns.data.pronouns.requests.RequestedPronounsYmlStorage;
import org.enchantedskies.EnchantedStorage.IOHandler;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.HashMap;

public class PronounsManager {
    private PronounsIOHandler pronounsIOHandler;
    private IOHandler<RequestedPronouns, String> requestsIOHandler;
    private static final HashMap<Integer, Pronouns> pronounsCache = new HashMap<>();
    private static final HashMap<Integer, RequestedPronouns> requestedPronounsCache = new HashMap<>();

    public PronounsManager() {
        // Adds empty pronouns into cache
        pronounsCache.put(-1, new Pronouns(-1, "", null));

        reload();
    }
    public void reload() {
        if (pronounsIOHandler != null) pronounsIOHandler.disableIOHandler();
        if (requestsIOHandler != null) requestsIOHandler.disableIOHandler();

        String storageType = SimplyPronouns.getConfigManager().getDatabaseType();
        switch (storageType.toLowerCase()) {
            case "yml" -> {
                pronounsIOHandler = new PronounsIOHandler(new PronounsYmlStorage());
                requestsIOHandler = new IOHandler<>(new RequestedPronounsYmlStorage());
            }
            case "mysql" -> {
                pronounsIOHandler = new PronounsIOHandler(new PronounsMysqlStorage());
                requestsIOHandler = new IOHandler<>(new RequestedPronounsMysqlStorage());
            }
        }
    }

    @NotNull
    public Pronouns getPronouns(int id) {
        return id >= 0 && pronounsCache.containsKey(id) ? pronounsCache.get(id) : pronounsCache.get(-1);
    }

    public void loadPronouns(int id) {
        pronounsIOHandler.loadData(id).thenAccept((pronouns) -> pronounsCache.put(pronouns.id(), pronouns));
    }

    public void loadAllPronouns() {

    }

    @NotNull
    public Collection<RequestedPronouns> getAllRequestedPronouns() {
        return requestedPronounsCache.values();
    }

    public void loadRequestedPronouns(int id) {

    }

    public void loadAllRequestedPronouns() {

    }

    public boolean disableIoHandlers() {
        return pronounsIOHandler.disableIOHandler() && requestsIOHandler.disableIOHandler();
    }
}
