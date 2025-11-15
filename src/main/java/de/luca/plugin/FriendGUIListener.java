package de.luca.plugin;

import de.luca.plugin.FriendManager;
import de.luca.plugin.Home;
import de.luca.plugin.HomeMainGUI;
import de.luca.plugin.LucaCrafterPlugin;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;

import java.util.Map;
import java.util.UUID;

public class FriendGUIListener implements Listener {

    private final LucaCrafterPlugin plugin;

    public FriendGUIListener(LucaCrafterPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onClick(InventoryClickEvent e) {

        if (!e.getView().getTitle().equals("§bDeine Freunde")) return;

        e.setCancelled(true);

        if (e.getCurrentItem() == null || e.getCurrentItem().getItemMeta() == null)
            return;

        Player p = (Player) e.getWhoClicked();

        if (e.getCurrentItem().getType() != Material.PLAYER_HEAD) return;

        String friendName = ChatColor.stripColor(
                e.getCurrentItem().getItemMeta().getDisplayName()
        );

        UUID friendUUID = null;

        for (UUID u : plugin.getFriendManager().getFriends(p.getUniqueId())) {
            if (plugin.getFriendManager().getName(u).equals(friendName)) {
                friendUUID = u;
                break;
            }
        }

        if (friendUUID == null) {
            p.sendMessage("§cFehler bei der Zuordnung!");
            return;
        }

        // Rechtsklick → entfernen
        if (e.isRightClick()) {
            if (plugin.getFriendManager().removeFriend(p.getUniqueId(), friendUUID)) {
                p.sendMessage("§cDu bist nicht mehr mit §e" + friendName + " §cbefreundet.");
            }
            p.closeInventory();
            p.openInventory(new FriendGUI(plugin, p).getInventory());
            return;
        }

        // Linksklick → Homes des Freundes ansehen
        if (e.isLeftClick()) {
            Map<String, Home> homes = plugin.getHomeManager().getHomes(friendUUID);

            if (homes.isEmpty()) {
                p.sendMessage("§eDieser Spieler hat keine sichtbaren Homes.");
                return;
            }

            p.openInventory(new FriendHomesGUI(plugin, p, friendUUID, friendName).getInventory());
        }
    }
}
