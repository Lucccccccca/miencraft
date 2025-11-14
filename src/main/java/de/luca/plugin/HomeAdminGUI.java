package de.luca.plugin;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Map;

public class HomeAdminGUI {

    private final Inventory inv;

    public HomeAdminGUI(LucaCrafterPlugin plugin, Player admin, OfflinePlayer target) {
        this.inv = Bukkit.createInventory(null, 54, "§cHomes von " + target.getName());

        Map<String, Home> homes = plugin.getHomeManager().getHomes(target.getUniqueId());

        int slot = 0;
        for (Home h : homes.values()) {
            ItemStack item = new ItemStack(Material.BOOK);
            ItemMeta meta = item.getItemMeta();

            meta.setDisplayName("§b" + h.getName());
            meta.setLore(java.util.Arrays.asList(
                    "§7Klicke zum Teleport.",
                    "§7Rechtsklick zum Löschen."
            ));
            item.setItemMeta(meta);

            inv.setItem(slot++, item);
        }
    }

    public Inventory getInventory() {
        return inv;
    }
}
    