package de.luca.plugin;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class PermissionInspectorGUI {

    public static void open(Player viewer, Player target) {

        Inventory inv = Bukkit.createInventory(null, 54,
                "Rechte von " + target.getName());

        for (String perm : viewer.getServer().getPluginManager()
                .getPermissions().stream().map(p -> p.getName()).toList()) {

            boolean has = target.hasPermission(perm);

            ItemStack item = new ItemStack(has ? Material.LIME_CONCRETE : Material.RED_CONCRETE);
            ItemMeta meta = item.getItemMeta();
            meta.setDisplayName((has ? ChatColor.GREEN : ChatColor.RED) + perm);
            item.setItemMeta(meta);

            inv.addItem(item);
        }

        viewer.openInventory(inv);
    }
}
