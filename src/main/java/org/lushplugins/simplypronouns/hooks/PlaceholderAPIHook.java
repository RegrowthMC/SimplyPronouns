package org.lushplugins.simplypronouns.hooks;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.jetbrains.annotations.NotNull;
import org.lushplugins.lushlib.hook.Hook;
import org.lushplugins.simplypronouns.SimplyPronouns;
import org.bukkit.entity.Player;
import org.lushplugins.simplypronouns.data.PronounsUser;
import org.lushplugins.simplypronouns.pronouns.Pronouns;

public class PlaceholderAPIHook extends Hook {
    private Expansion expansion;

    public PlaceholderAPIHook() {
        super("PlaceholderAPI");
    }

    @Override
    protected void onEnable() {
        expansion = new Expansion();
        expansion.register();
    }

    @Override
    protected void onDisable() {
        if (expansion != null) {
            expansion.unregister();
            expansion = null;
        }
    }

    public static class Expansion extends PlaceholderExpansion {

        public String onPlaceholderRequest(Player player, @NotNull String params) {
            if (player == null) {
                return null;
            }

            if (params.equals("pronouns")) {
                PronounsUser user = SimplyPronouns.getInstance().getUserManager().getCachedUser(player.getUniqueId());
                if (user == null) {
                    return "";
                }

                Pronouns pronouns = user.getPronouns();
                if (pronouns == null || pronouns.areAnyDenied()) {
                    return "";
                }

                if (SimplyPronouns.getInstance().getConfigManager().requiresApproval() && !pronouns.areAllApproved()) {
                    return "";
                }

                return SimplyPronouns.getInstance().getConfigManager().getPlaceholderFormat()
                    .replace("%pronouns%", pronouns.asString());
            }

            return null;
        }

        public boolean persist() {
            return true;
        }

        public boolean canRegister() {
            return true;
        }

        public @NotNull String getIdentifier() {
            return "simplypronouns";
        }

        public @NotNull String getAuthor() {
            return SimplyPronouns.getInstance().getDescription().getAuthors().toString();
        }

        public @NotNull String getVersion() {
            return SimplyPronouns.getInstance().getDescription().getVersion();
        }
    }
}

