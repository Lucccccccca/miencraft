package de.luca.plugin;

import de.luca.plugin.*;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;

import java.util.UUID;

public class FriendListListener implements Listener {

    private final LucaCrafterPlugin plugin;

    public FriendListListener(LucaCrafterPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onClick(InventoryClickEvent e) {

        String title = e.getView().getTitle();
        if (!title.startsWith("§bFreunde:")) return;

        e.setCancelled(true);

        if (e.getCurrentItem() == null || e.getCurrentItem().getItemMeta() == null)
            return;

        Player p = (Player) e.getWhoClicked();
        String name = ChatColor.stripColor(e.getCurrentItem().getItemMeta().getDisplayName());
        FriendManager fm = plugin.getFriendManager();

        UUID targetUUID = null;
        // Grobe Suche über alle bekannten UUIDs aus Managersets:
        for (UUID u : fm.getFriends(p.getUniqueId())) {
            if (fm.getName(u).equalsIgnoreCase(name)) {
                targetUUID = u;
                break;
            }
        }
        if (targetUUID == null) {
            for (UUID u : fm.getRequestsReceived(p.getUniqueId())) {
                if (fm.getName(u).equalsIgnoreCase(name)) {
                    targetUUID = u;
                    break;
                }
            }
        }
        if (targetUUID == null) {
            for (UUID u : fm.getBlocked(p.getUniqueId())) {
                if (fm.getName(u).equalsIgnoreCase(name)) {
                    targetUUID = u;
                    break;
                }
            }
        }
        if (targetUUID == null) {
            for (UUID u : fm.getFavorites(p.getUniqueId())) {
                if (fm.getName(u).equalsIgnoreCase(name)) {
                    targetUUID = u;
                    break;
                }
            }
        }

        if (targetUUID == null) {
            p.sendMessage("§cSpieler konnte nicht zugeordnet werden.");
            return;
        }

        UUID me = p.getUniqueId();

        boolean left = e.isLeftClick();
        boolean right = e.isRightClick();
        boolean shift = e.isShiftClick();

        if (title.equals("§bFreunde: Anfragen")) {
            if (left) {
                if (fm.acceptRequest(me, targetUUID)) {
                    p.sendMessage("§aDu bist nun mit §e" + fm.getName(targetUUID) + " §abefreundet.");
                }
            } else if (right) {
                if (fm.denyRequest(me, targetUUID)) {
                    p.sendMessage("§eDu hast die Anfrage von §e" + fm.getName(targetUUID) + " §eabgelehnt.");
                }
            }
            p.openInventory(new FriendListGUI(plugin, p, FriendListType.REQUESTS).getInventory());
            return;
        }

        if (title.equals("§bFreunde: Blockierte")) {
            if (right) {
                if (fm.unblock(me, targetUUID)) {
                    p.sendMessage("§aDu hast §e" + fm.getName(targetUUID) + " §awieder entblockt.");
                }
                p.openInventory(new FriendListGUI(plugin, p, FriendListType.BLOCKED).getInventory());
            }
            return;
        }

        // Favoriten-Liste
        if (title.equals("§bFreunde: Favoriten")) {
            if (left) {
                // OPTIONAL: Homes des Freundes ansehen (Hook)
                p.sendMessage("§e(Homes des Freundes können hier noch angebunden werden.)");
            } else if (right) {
                if (fm.removeFriend(me, targetUUID)) {
                    p.sendMessage("§cDu bist nicht mehr mit §e" + fm.getName(targetUUID) + " §cbefreundet.");
                }
            } else if (shift && left) {
                if (fm.toggleFavorite(me, targetUUID)) {
                    p.sendMessage("§aFavorit gesetzt.");
                } else {
                    p.sendMessage("§eFavorit entfernt.");
                }
            } else if (shift && right) {
                if (fm.block(me, targetUUID)) {
                    p.sendMessage("§cDu hast §e" + fm.getName(targetUUID) + " §cblockiert.");
                }
            }
            p.openInventory(new FriendListGUI(plugin, p, FriendListType.FAVORITES).getInventory());
            return;
        }

        // ONLINE / OFFLINE Liste
        if (title.equals("§bFreunde: Online") || title.equals("§bFreunde: Offline")) {

            if (left && !shift) {
                // Hier könntest du Friend-Homes-GUI aufrufen
                p.sendMessage("§e(Hier kann später das Homes-GUI des Freundes eingebunden werden.)");
                return;
            }

            if (right && !shift) {
                if (fm.removeFriend(me, targetUUID)) {
                    p.sendMessage("§cDu bist nicht mehr mit §e" + fm.getName(targetUUID) + " §cbefreundet.");
                }
            } else if (shift && right) {
                if (fm.block(me, targetUUID)) {
                    p.sendMessage("§cDu hast §e" + fm.getName(targetUUID) + " §cblockiert.");
                }
            } else if (shift && left) {
                boolean nowFav = fm.toggleFavorite(me, targetUUID);
                p.sendMessage(nowFav
                        ? "§aAls Favorit markiert."
                        : "§eNicht mehr Favorit.");
            }

            FriendListType type = title.equals("§bFreunde: Online")
                    ? FriendListType.ONLINE
                    : FriendListType.OFFLINE;
            p.openInventory(new FriendListGUI(plugin, p, type).getInventory());
        }
    }
}
