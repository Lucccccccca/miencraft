package de.luca.plugin;

import de.luca.plugin.LucaCrafterPlugin;
import org.bukkit.entity.Player;
import org.bukkit.event.*;
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
            case 10: // Anfragen
                p.openInventory(new FriendListGUI(plugin, p, FriendListType.REQUESTS).getInventory());
                break;
            case 12: // Online
                p.openInventory(new FriendListGUI(plugin, p, FriendListType.ONLINE).getInventory());
                break;
            case 14: // Offline
                p.openInventory(new FriendListGUI(plugin, p, FriendListType.OFFLINE).getInventory());
                break;
            case 16: // Favoriten
                p.openInventory(new FriendListGUI(plugin, p, FriendListType.FAVORITES).getInventory());
                break;
            case 22: // Blockierte
                p.openInventory(new FriendListGUI(plugin, p, FriendListType.BLOCKED).getInventory());
                break;
            default:
                break;
        }
    }
}
