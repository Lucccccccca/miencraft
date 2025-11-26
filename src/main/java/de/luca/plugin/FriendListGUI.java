package de.luca.plugin;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.*;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.*;

public class FriendListGUI {

    private static ItemStack filler() {
        ItemStack it = new ItemStack(Material.BLACK_STAINED_GLASS_PANE);
        ItemMeta im = it.getItemMeta();
        im.setDisplayName(" ");
        it.setItemMeta(im);
        return it;
    }

    public static Inventory build(LucaCrafterPlugin plugin, Player viewer, FriendListType type) {
        String title;
        switch (type) {
            case REQUESTS:  title = "§bFreunde: Anfragen"; break;
            case ONLINE:    title = "§bFreunde: Online";   break;
            case OFFLINE:   title = "§bFreunde: Offline";  break;
            case FAVORITES: title = "§bFreunde: Favoriten";break;
            case BLOCKED:   title = "§bFreunde: Blockierte";break;
            default:        title = "§bFreunde";           break;
        }

        Inventory inv = Bukkit.createInventory(null, 54, title);
        ItemStack fi = filler();
        for (int i = 0; i < inv.getSize(); i++) inv.setItem(i, fi);

        FriendManager fm = plugin.getFriendManager();
        UUID uuid = viewer.getUniqueId();

        Set<UUID> list;
        switch (type) {
            case REQUESTS:  list = fm.getRequestsReceived(uuid); break;
            case BLOCKED:   list = fm.getBlocked(uuid);          break;
            case FAVORITES: list = fm.getFavorites(uuid);        break;
            default:        list = fm.getFriends(uuid);          break;
        }

        if (list.isEmpty()) {
            ItemStack no = new ItemStack(Material.BARRIER);
            ItemMeta meta = no.getItemMeta();
            meta.setDisplayName("§cKeine Einträge");
            no.setItemMeta(meta);
            inv.setItem(22, no);
            return inv;
        }

        int slot = 10;
        for (UUID u : list) {
            OfflinePlayer op = Bukkit.getOfflinePlayer(u);
            if (type == FriendListType.ONLINE) {
                if (op.getPlayer() == null || !op.getPlayer().isOnline()) continue;
            } else if (type == FriendListType.OFFLINE) {
                if (op.getPlayer() != null && op.getPlayer().isOnline()) continue;
            }

            ItemStack head = new ItemStack(Material.PLAYER_HEAD);
            SkullMeta sm = (SkullMeta) head.getItemMeta();
            sm.setOwningPlayer(op);
            sm.setDisplayName("§a" + (op.getName() != null ? op.getName() : u.toString()));

            List<String> lore = new ArrayList<>();
            switch (type) {
                case REQUESTS:
                    lore.add("§7Freundschaftsanfrage");
                    lore.add("§aLinksklick: annehmen");
                    lore.add("§cRechtsklick: ablehnen");
                    break;
                case BLOCKED:
                    lore.add("§7Du hast diesen Spieler blockiert.");
                    lore.add("§cRechtsklick: Blockierung aufheben");
                    break;
                default:
                    lore.add("§7Linksklick: Optionen");
                    lore.add("§cShift-Rechtsklick: Entfernen");
                    lore.add("§4Shift-Linksklick: Blockieren");
                    break;
            }

            sm.setLore(lore);
            head.setItemMeta(sm);
            inv.setItem(slot, head);

            slot++;
            if (slot == 17) slot = 19;
            if (slot == 26) slot = 28;
            if (slot == 35) slot = 37;
            if (slot >= 44) break;
        }

        // Zurück
        ItemStack back = new ItemStack(Material.ARROW);
        var bm = back.getItemMeta();
        bm.setDisplayName("§7↩ Zurück");
        back.setItemMeta(bm);
        inv.setItem(49, back);

        return inv;
    }
}
