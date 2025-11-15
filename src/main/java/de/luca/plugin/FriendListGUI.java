package de.luca.plugin;

import de.luca.plugin.FriendManager;
import de.luca.plugin.LucaCrafterPlugin;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;   
import org.bukkit.inventory.meta.SkullMeta; 

import java.util.*;

public class FriendListGUI {

    private final Inventory inv;

    public FriendListGUI(LucaCrafterPlugin plugin, Player viewer, FriendListType type) {

        String title;
        switch (type) {
            case REQUESTS:
                title = "§bFreunde: Anfragen";
                break;
            case ONLINE:
                title = "§bFreunde: Online";
                break;
            case OFFLINE:
                title = "§bFreunde: Offline";
                break;
            case FAVORITES:
                title = "§bFreunde: Favoriten";
                break;
            case BLOCKED:
                title = "§bFreunde: Blockierte";
                break;
            default:
                title = "§bFreunde";
        }

        this.inv = Bukkit.createInventory(null, 54, title);

        FriendManager fm = plugin.getFriendManager();
        UUID uuid = viewer.getUniqueId();

        Set<UUID> list;

        switch (type) {
            case REQUESTS:
                list = fm.getRequestsReceived(uuid);
                break;
            case BLOCKED:
                list = fm.getBlocked(uuid);
                break;
            case FAVORITES:
                list = fm.getFavorites(uuid);
                break;
            default:
                list = fm.getFriends(uuid);
                break;
        }

        ItemStack filler = new ItemStack(Material.BLACK_STAINED_GLASS_PANE);
        ItemMeta fmMeta = filler.getItemMeta();
        fmMeta.setDisplayName(" ");
        filler.setItemMeta(fmMeta);
        for (int i = 0; i < inv.getSize(); i++) inv.setItem(i, filler);

        if (list.isEmpty()) {
            ItemStack no = new ItemStack(Material.BARRIER);
            ItemMeta meta = no.getItemMeta();
            meta.setDisplayName("§cKeine Einträge");
            switch (type) {
                case REQUESTS:
                    meta.setLore(Collections.singletonList("§7Du hast aktuell keine Anfragen."));
                    break;
                case ONLINE:
                    meta.setLore(Collections.singletonList("§7Keine Freunde online."));
                    break;
                case OFFLINE:
                    meta.setLore(Collections.singletonList("§7Keine Freunde offline."));
                    break;
                case FAVORITES:
                    meta.setLore(Collections.singletonList("§7Du hast keine Favoriten."));
                    break;
                case BLOCKED:
                    meta.setLore(Collections.singletonList("§7Du hast niemanden blockiert."));
                    break;
            }
            no.setItemMeta(meta);
            inv.setItem(22, no);
            return;
        }

        int slot = 10;
        for (UUID u : list) {

            OfflinePlayer op = Bukkit.getOfflinePlayer(u);
            String name = op.getName() != null ? op.getName() : u.toString();

            // Filter online/offline bei den entsprechenden Typen:
            if (type == FriendListType.ONLINE) {
                if (op.getPlayer() == null || !op.getPlayer().isOnline()) continue;
            } else if (type == FriendListType.OFFLINE) {
                if (op.getPlayer() != null && op.getPlayer().isOnline()) continue;
            }

            ItemStack head = new ItemStack(Material.PLAYER_HEAD);
            SkullMeta meta = (SkullMeta) head.getItemMeta();
            meta.setOwningPlayer(op);
            meta.setDisplayName("§a" + name);

            List<String> lore = new ArrayList<>();

            switch (type) {
                case REQUESTS:
                    lore.add("§7Freundschaftsanfrage von §e" + name);
                    lore.add("");
                    lore.add("§aLinksklick: §7Anfrage akzeptieren");
                    lore.add("§cRechtsklick: §7Anfrage ablehnen");
                    break;
                case BLOCKED:
                    lore.add("§7Du hast diesen Spieler blockiert.");
                    lore.add("");
                    lore.add("§cRechtsklick: §7Blockierung aufheben");
                    break;
                case FAVORITES:
                    lore.add("§7Dieser Freund ist ein Favorit.");
                    lore.add("");
                    lore.add("§aLinksklick: §7Homes ansehen");
                    lore.add("§cRechtsklick: §7Freund entfernen");
                    lore.add("§eShift-Rechtsklick: §7Blockieren");
                    lore.add("§eShift-Linksklick: §7Favorit entfernen");
                    break;
                default: // ONLINE / OFFLINE (normale Freunde)
                    lore.add("§7Freundschaft: §aBestätigt");
                    lore.add("");
                    lore.add("§aLinksklick: §7Homes des Freundes ansehen");
                    lore.add("§cRechtsklick: §7Freund entfernen");
                    lore.add("§eShift-Rechtsklick: §7Blockieren");
                    lore.add("§eShift-Linksklick: §7Als Favorit togglen");
                    break;
            }

            meta.setLore(lore);
            head.setItemMeta(meta);

            inv.setItem(slot, head);

            slot++;
            if (slot == 17) slot = 19;
            if (slot == 26) slot = 28;
            if (slot >= 35) break;
        }
    }

    public Inventory getInventory() {
        return inv;
    }
}
