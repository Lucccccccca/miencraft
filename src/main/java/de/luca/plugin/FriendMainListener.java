package de.luca.plugin;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;

public class FriendMainListener implements Listener {

    private final LucaCrafterPlugin plugin;

    public FriendMainListener(LucaCrafterPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onClick(InventoryClickEvent e) {

        if (!e.getView().getTitle().equals("§bFreunde-Menü")) return;

        e.setCancelled(true);
        if (e.getCurrentItem() == null || e.getCurrentItem().getItemMeta() == null) return;

        Player p = (Player) e.getWhoClicked();
        int slot = e.getRawSlot();

        switch (slot) {

            case 10: // Freunde Liste (Online)
                p.openInventory(FriendListGUI.build(plugin, p, FriendListType.ONLINE));
                break;

            case 12: // Online Freunde
                p.openInventory(FriendListGUI.build(plugin, p, FriendListType.ONLINE));
                break;

            case 14: // Offline Freunde
                p.openInventory(FriendListGUI.build(plugin, p, FriendListType.OFFLINE));
                break;

            case 16: // Favoriten
                p.openInventory(FriendListGUI.build(plugin, p, FriendListType.FAVORITES));
                break;

            case 22: // Blockierte
                p.openInventory(FriendListGUI.build(plugin, p, FriendListType.BLOCKED));
                break;

            case 13: // Freund hinzufügen
                p.openInventory(FreundeAddGUI.build(p)); 
                break;

            default:
                break;
        }
    }
}
