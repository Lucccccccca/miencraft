package de.luca.plugin;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class SettingsServerGUI {

    public static void open(Player p, LucaCrafterPlugin plugin) {
        Inventory inv = Bukkit.createInventory(null, 27, "§e⚙ Server-Einstellungen");

        addItem(inv, 11, Material.WHEAT, "§a🌾 Farm-Protection");
        addItem(inv, 13, Material.CREEPER_HEAD, "§c💥 Anti-Creeper");
        addItem(inv, 15, Material.IRON_SWORD, "§f⚔ PvP umschalten");
        addItem(inv, 26, Material.BARRIER, "§cZurück");

        p.openInventory(inv);
    }

    private static void addItem(Inventory inv, int slot, Material mat, String name) {
        ItemStack item = new ItemStack(mat);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(name);
        item.setItemMeta(meta);
        inv.setItem(slot, item);
    }
}
