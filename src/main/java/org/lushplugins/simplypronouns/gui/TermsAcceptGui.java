package org.lushplugins.simplypronouns.gui;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.lushplugins.lushlib.gui.button.SimpleItemButton;
import org.lushplugins.lushlib.gui.inventory.Gui;
import org.lushplugins.lushlib.utils.DisplayItemStack;
import org.lushplugins.simplypronouns.SimplyPronouns;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class TermsAcceptGui extends Gui {
    private final Runnable onDecline;
    private boolean accepted = false;

    private TermsAcceptGui(String title, String terms, Runnable onAccept, Runnable onDecline, Player player) {
        super(27, title, player);
        this.onDecline = onDecline;

        List<String> lore = Arrays.stream(terms.split("\\n"))
            .map(line -> "&7" + line)
            .collect(Collectors.toList());
        lore.add("");
        lore.add("<#b7faa2>ᴄʟɪᴄᴋ ᴛᴏ ᴀᴄᴄᴇᴘᴛ</#b7faa2>");

        addButton(13, new SimpleItemButton(DisplayItemStack.builder()
            .setType(Material.LIME_DYE)
            .setDisplayName("&rAccept Terms")
            .setLore(lore)
            .build(),
            (event) -> {
                accepted = true;
                this.close();
                onAccept.run();
            }
        ));
    }

    @Override
    public void onClick(InventoryClickEvent event) {
        super.onClick(event, true);
    }

    @Override
    public void onClose(InventoryCloseEvent event) {
        super.onClose(event);

        if (!accepted) {
            onDecline.run();
        }
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String title = "Accept Terms";
        private String terms = "";
        private Runnable onAccept;
        private Runnable onDecline;

        private Builder() {}

        public Builder title(String title) {
            this.title = title;
            return this;
        }

        public Builder terms(String terms) {
            this.terms = terms;
            return this;
        }

        public Builder onAccept(Runnable runnable) {
            this.onAccept = runnable;
            return this;
        }

        public Builder onDecline(Runnable runnable) {
            this.onDecline = runnable;
            return this;
        }

        public TermsAcceptGui build(Player player) {
            return new TermsAcceptGui(title, terms, onAccept, onDecline, player);
        }

        public TermsAcceptGui open(Player player) {
            TermsAcceptGui gui = build(player);

            if (!Bukkit.isPrimaryThread()) {
                Bukkit.getScheduler().runTask(SimplyPronouns.getInstance(), gui::open);
            } else {
                gui.open();
            }

            return gui;
        }
    }
}
