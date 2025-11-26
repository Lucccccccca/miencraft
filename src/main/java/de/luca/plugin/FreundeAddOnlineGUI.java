package de.luca.plugin;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.*;
import org.bukkit.inventory.meta.SkullMeta;

public class FreundeAddOnlineGUI {

    public static Inventory build() {
        Inventory inv = Bukkit.createInventory(null, 54, "§aOnline-Spieler");

        ItemStack filler = new ItemStack(Material.BLACK_STAINED_GLASS_PANE);
        inv.setItem(49, backButton());

        int slot = 10;
        for (Player pl : Bukkit.getOnlinePlayers()) {
            ItemStack head = new ItemStack(Material.PLAYER_HEAD);
            SkullMeta sm = (SkullMeta) head.getItemMeta();
            OfflinePlayer op = Bukkit.getOfflinePlayer(pl.getUniqueId());
            sm.setOwningPlayer(op);
            sm.setDisplayName("§a" + pl.getName());
            head.setItemMeta(sm);

            inv.setItem(slot, head);

            slot++;
            if (slot == 17) slot = 19;
            if (slot == 26) slot = 28;
            if (slot == 35) slot = 37;
            if (slot >= 44) break;
        }

        // Filler
        for (int i = 0; i < inv.getSize(); i++) if (inv.getItem(i) == null) inv.setItem(i, filler);
        return inv;
    }

    private static ItemStack backButton() {
        ItemStack it = new ItemStack(Material.ARROW);
        var im = it.getItemMeta();
        im.setDisplayName("§7↩ Zurück");
        it.setItemMeta(im);
        return it;
    }
}
