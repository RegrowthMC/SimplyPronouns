package org.lushplugins.simplypronouns.commands.example;

import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.lushplugins.lushlib.libraries.chatcolor.ChatColorHandler;
import org.lushplugins.simplypronouns.SimplyPronouns;
import org.lushplugins.simplypronouns.data.PronounsUser;

@Command(
    name = "set",
    aliases = {"change", "update"},
    permission = "simplypronouns.set"
)
public class NewSetPronounsCommand {

    public void execute(CommandSender sender, OfflinePlayer player, String requestedPronouns) {
        PronounsUser user = SimplyPronouns.getInstance().getUserManager().getCachedUser(player.getUniqueId());
        if (user == null) {
            ChatColorHandler.sendMessage(sender, SimplyPronouns.getInstance().getConfigManager().getMessage("failed-to-set"));
            return;
        }

        SimplyPronouns.getInstance().getPronounManager().compilePronouns(requestedPronouns.split("/")).thenAccept(pronouns -> {
            user.setPronouns(pronouns);

            ChatColorHandler.sendMessage(sender, SimplyPronouns.getInstance().getConfigManager().getMessage("pronouns-set")
                .replace("%pronouns%", requestedPronouns));
        });
    }
}
