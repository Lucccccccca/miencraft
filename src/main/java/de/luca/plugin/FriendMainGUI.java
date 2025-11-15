package de.luca.plugin;

import de.luca.plugin.FriendManager;
import de.luca.plugin.LucaCrafterPlugin;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;   // ✅
import java.util.Arrays;

public class FriendMainGUI {

    private final Inventory inv;

    public FriendMainGUI(LucaCrafterPlugin plugin, Player p) {

        this.inv = Bukkit.createInventory(null, 27, "§bFreunde-Menü");

        FriendManager fm = plugin.getFriendManager();
        int requests = fm.getRequestsReceived(p.getUniqueId()).size();
        int friendsCount = fm.getFriends(p.getUniqueId()).size();
        int blocked = fm.getBlocked(p.getUniqueId()).size();
        int favorites = fm.getFavorites(p.getUniqueId()).size();

        ItemStack filler = new ItemStack(Material.GRAY_STAINED_GLASS_PANE);
        ItemMeta fmMeta = filler.getItemMeta();
        fmMeta.setDisplayName(" ");
        filler.setItemMeta(fmMeta);
        for (int i = 0; i < inv.getSize(); i++) inv.setItem(i, filler);

        // 📬 Ausstehende Anfragen
        ItemStack requestsItem = new ItemStack(Material.WRITABLE_BOOK);
        ItemMeta rMeta = requestsItem.getItemMeta();
        rMeta.setDisplayName("§a📬 Ausstehende Anfragen");
        rMeta.setLore(Arrays.asList(
                "§7Offene Anfragen: §e" + requests,
                "",
                "§eLinksklick: Anzeigen & Verwalten"
        ));
        requestsItem.setItemMeta(rMeta);
        inv.setItem(10, requestsItem);

        // 🟢 Freunde Online
        ItemStack onlineItem = new ItemStack(Material.LIME_DYE);
        ItemMeta oMeta = onlineItem.getItemMeta();
        oMeta.setDisplayName("§a🟢 Freunde Online");
        oMeta.setLore(Arrays.asList(
                "§7Gesamt: §e" + friendsCount,
                "",
                "§eLinksklick: Online-Freunde anzeigen"
        ));
        onlineItem.setItemMeta(oMeta);
        inv.setItem(12, onlineItem);

        // 🔴 Freunde Offline
        ItemStack offlineItem = new ItemStack(Material.RED_DYE);
        ItemMeta offMeta = offlineItem.getItemMeta();
        offMeta.setDisplayName("§c🔴 Freunde Offline");
        offMeta.setLore(Arrays.asList(
                "§7Gesamt: §e" + friendsCount,
                "",
                "§eLinksklick: Offline-Freunde anzeigen"
        ));
        offlineItem.setItemMeta(offMeta);
        inv.setItem(14, offlineItem);

        // ⭐ Favoriten
        ItemStack favItem = new ItemStack(Material.NETHER_STAR);
        ItemMeta fMeta = favItem.getItemMeta();
        fMeta.setDisplayName("§e⭐ Favoriten");
        fMeta.setLore(Arrays.asList(
                "§7Favoriten: §e" + favorites,
                "",
                "§eLinksklick: Favoriten anzeigen"
        ));
        favItem.setItemMeta(fMeta);
        inv.setItem(16, favItem);

        // ⛔ Blockierte
        ItemStack blockItem = new ItemStack(Material.BARRIER);
        ItemMeta bMeta = blockItem.getItemMeta();
        bMeta.setDisplayName("§4⛔ Blockierte Spieler");
        bMeta.setLore(Arrays.asList(
                "§7Blockiert: §e" + blocked,
                "",
                "§eLinksklick: Blockierte anzeigen"
        ));
        blockItem.setItemMeta(bMeta);
        inv.setItem(22, blockItem);
    }

    public Inventory getInventory() {
        return inv;
    }
}
