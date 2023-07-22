package me.dave.simplypronouns.hooks;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import me.dave.simplypronouns.SimplyPronouns;
import me.dave.simplypronouns.data.user.PronounsUser;
import org.bukkit.entity.Player;

public class PlaceholderAPIHook extends PlaceholderExpansion {

    public String onPlaceholderRequest(Player player, String params) {
        if (player == null) return null;

        PronounsUser pronounsUser = SimplyPronouns.getUserManager().getPronounsUser(player);
        if (params.equals("pronouns")) {
            return SimplyPronouns.getPronounsManager().getPronouns(pronounsUser.getPronounsId()).pronouns();
        }

        return null;
    }

    public boolean persist() {
        return true;
    }

    public boolean canRegister() {
        return true;
    }

    public String getIdentifier() {
        return "simplypronouns";
    }

    public String getAuthor() {
        return SimplyPronouns.getInstance().getDescription().getAuthors().toString();
    }

    public String getVersion() {
        return SimplyPronouns.getInstance().getDescription().getVersion();
    }
}

