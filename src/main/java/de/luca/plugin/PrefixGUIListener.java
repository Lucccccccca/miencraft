package de.luca.plugin;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.UUID;

public class PrefixGUIListener implements Listener {

    private final LucaCrafterPlugin plugin;
    private final PlayerPrefixManager prefixManager;
    private final PrefixUpdater prefixUpdater;
    private final PrefixChatListener chatListener;

    public PrefixGUIListener(LucaCrafterPlugin plugin, PrefixChatListener chatListener) {
        this.plugin = plugin;
        this.prefixManager = plugin.getPlayerPrefixManager();
        this.prefixUpdater = plugin.getPrefixUpdater();
        this.chatListener = chatListener;
    }

    @EventHandler
    public void onClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player player)) return;

        Inventory inv = event.getInventory();
        String title = event.getView().getTitle();

        if (!title.equals(PrefixGUI.TITLE) && !title.equals(PrefixColorGUI.TITLE)) return;

        event.setCancelled(true);

        ItemStack clicked = event.getCurrentItem();
        if (clicked == null || clicked.getType() == Material.AIR) return;

        UUID uuid = player.getUniqueId();

        // --------------------------- Prefix-HauptGUI ---------------------------
        if (title.equals(PrefixGUI.TITLE)) {

            int slot = event.getRawSlot();

            if (slot == PrefixGUI.SLOT_TOGGLE) {
                boolean enabled = prefixManager.isCustomEnabled(uuid);
                prefixManager.setCustomEnabled(uuid, !enabled);
                player.sendMessage("§8[§aPrefix§8] §7Custom-Prefix ist jetzt: " +
                        (!enabled ? "§2AKTIV" : "§cDEAKTIVIERT"));
                prefixUpdater.update(player);
                PrefixGUI.open(plugin, player);
                return;
            }

            if (slot == PrefixGUI.SLOT_TEXT) {
                player.closeInventory();
                chatListener.startPrefixInput(player);
                return;
            }

            if (slot == PrefixGUI.SLOT_COLOR) {
                PrefixColorGUI.open(player);
                return;
            }

            if (slot == PrefixGUI.SLOT_RESET) {
                prefixManager.setCustomEnabled(uuid, false);
                prefixManager.setCustomText(uuid, "");
                prefixManager.setColorCode(uuid, "&f");
                player.sendMessage("§8[§aPrefix§8] §7Custom-Prefix zurückgesetzt. Rollen-Prefix wird verwendet.");
                prefixUpdater.update(player);
                PrefixGUI.open(plugin, player);
                return;
            }

            return;
        }

        // --------------------------- Farb-GUI ---------------------------
        if (title.equals(PrefixColorGUI.TITLE)) {
            Material type = clicked.getType();

            String code = null;
            switch (type) {
                case WHITE_WOOL -> code = "&f";
                case LIGHT_GRAY_WOOL -> code = "&7";
                case YELLOW_WOOL -> code = "&e";
                case GREEN_WOOL -> code = "&a";
                case LIGHT_BLUE_WOOL -> code = "&b";
                case BLUE_WOOL -> code = "&9";
                case RED_WOOL -> code = "&c";
                case BARRIER -> {
                    PrefixGUI.open(plugin, player);
                    return;
                }
                default -> {
                    return;
                }
            }

            prefixManager.setColorCode(uuid, code);
            player.sendMessage("§8[§aPrefix§8] §7Farbe gesetzt auf " +
                    ChatColor.translateAlternateColorCodes('&', code + "dies"));
            prefixUpdater.update(player);
            PrefixGUI.open(plugin, player);
        }
    }
}
