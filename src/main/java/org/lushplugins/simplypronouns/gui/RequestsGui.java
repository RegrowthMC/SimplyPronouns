package org.lushplugins.simplypronouns.gui;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.lushplugins.lushlib.gui.button.ItemButton;
import org.lushplugins.lushlib.gui.inventory.Gui;
import org.lushplugins.lushlib.gui.inventory.PagedGui;
import org.lushplugins.lushlib.utils.DisplayItemStack;
import org.lushplugins.simplypronouns.SimplyPronouns;
import org.lushplugins.simplypronouns.pronouns.Pronoun;
import org.lushplugins.simplypronouns.util.DiscordUtil;

import java.util.List;

public class RequestsGui extends PagedGui {

    public RequestsGui(Player player) {
        super(54, "Requests", player);
    }

    @Override
    public void refresh() {
        for (int slot : this.getButtons().keySet()) {
            setItem(slot, null);
        }

        clearButtons();
        SimplyPronouns.getInstance().getStorageManager().loadPronouns(Pronoun.Status.AWAITING).thenAccept(pronouns -> {
            for (int i = 0; i < 54; i++) {
                if (pronouns.size() <= i) {
                    break;
                }

                String pronoun = pronouns.get(i);
                addButton(i, new RequestButton(pronoun, this));
            }

            super.refresh();
        });
    }

    public static class RequestButton extends ItemButton {
        private final String pronoun;
        private final ItemStack item;
        private boolean clicked = false;

        public RequestButton(String pronoun, Gui gui) {
            super((ignored) -> gui.refresh());
            this.pronoun = pronoun;
            this.item = DisplayItemStack.builder()
                .setType(Material.PAPER)
                .setDisplayName("Review '%s'".formatted(pronoun))
                .setLore(List.of(
                    "",
                    "<#FFD392>ʟᴇғᴛ ᴄʟɪᴄᴋ</#FFD392> <#b7faa2>Accept</#b7faa2>",
                    "<#FFD392>ʀɪɢʜᴛ ᴄʟɪᴄᴋ</#FFD392> <#ff6969>Deny</#ff6969>"
                ))
                .build()
                .asItemStack();
        }

        @Override
        public void click(InventoryClickEvent event) {
            if (clicked) {
                return;
            }

            Pronoun.Status status = switch (event.getClick()) {
                case LEFT -> Pronoun.Status.APPROVED;
                case RIGHT -> Pronoun.Status.DENIED;
                default -> null;
            };

            if (status != null) {
                clicked = true;

                Pronoun cachedPronoun = SimplyPronouns.getInstance().getPronounManager().getCachedPronoun(this.pronoun);
                if (cachedPronoun != null) {
                    cachedPronoun.setStatus(status);
                } else {
                    SimplyPronouns.getInstance().getStorageManager().savePronounStatus(this.pronoun, status);
                }

                DiscordUtil.Message discordMessage = SimplyPronouns.getInstance().getConfigManager().getDiscordLog("request-" + status.name().toLowerCase());
                if (discordMessage != null) {
                    discordMessage.send((string) -> string
                        .replace("%input%", "Pronoun")
                        .replace("%content%", this.pronoun)
                        .replace("%player%", event.getWhoClicked().getName()));
                }

                super.click(event);
            }
        }

        @Override
        public ItemStack getItemStack(Player player) {
            return item;
        }
    }
}
