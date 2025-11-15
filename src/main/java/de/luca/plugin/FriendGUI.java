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

import java.util.*;

public class FriendGUI {

    private final Inventory inv;

    public FriendGUI(LucaCrafterPlugin plugin, Player p) {

        this.inv = Bukkit.createInventory(null, 54, "§bDeine Freunde");

        FriendManager fm = plugin.getFriendManager();
        Set<UUID> list = fm.getFriends(p.getUniqueId());

        // Füller
        ItemStack filler = new ItemStack(Material.GRAY_STAINED_GLASS_PANE);
        ItemMeta fmMeta = filler.getItemMeta();
        fmMeta.setDisplayName(" ");
        filler.setItemMeta(fmMeta);
        for (int i = 0; i < inv.getSize(); i++) inv.setItem(i, filler);

        // Wenn keine Freunde
        if (list.isEmpty()) {
            ItemStack no = new ItemStack(Material.BARRIER);
            ItemMeta meta = no.getItemMeta();
            meta.setDisplayName("§cKeine Freunde gefunden");
            meta.setLore(Collections.singletonList("§7Benutze §e/friend add <Name>"));
            no.setItemMeta(meta);
            inv.setItem(22, no);
            return;
        }

        // Freunde anzeigen
        int slot = 10;
        for (UUID u : list) {
            OfflinePlayer op = Bukkit.getOfflinePlayer(u);

            ItemStack head = new ItemStack(Material.PLAYER_HEAD);
            ItemMeta meta = head.getItemMeta();
            meta.setDisplayName("§a" + (op.getName() != null ? op.getName() : u.toString()));

            List<String> lore = new ArrayList<>();
            lore.add("§7Freundschaft: §aBestätigt");
            lore.add("");
            lore.add("§eLinksklick: Homes des Freundes ansehen");
            lore.add("§cRechtsklick: Entfernen");
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
