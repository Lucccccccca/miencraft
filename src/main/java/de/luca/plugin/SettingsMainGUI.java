package de.luca.plugin;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class SettingsMainGUI {

    public static void open(Player p, LucaCrafterPlugin plugin) {
        Inventory inv = Bukkit.createInventory(null, 9, "§b⚙ Einstellungen");

        // 🧍 Spieler-Einstellungen
        ItemStack player = new ItemStack(Material.PLAYER_HEAD);
        ItemMeta meta1 = player.getItemMeta();
        meta1.setDisplayName("§a🧍 Spieler-Einstellungen");
        player.setItemMeta(meta1);
        inv.setItem(3, player);

        // ⚙️ Server-Einstellungen (nur für OPs)
        if (p.isOp()) {
            ItemStack server = new ItemStack(Material.COMMAND_BLOCK);
            ItemMeta meta2 = server.getItemMeta();
            meta2.setDisplayName("§e⚙ Server-Einstellungen");
            server.setItemMeta(meta2);
            inv.setItem(5, server);
        }

        // ❌ Schließen
        ItemStack close = new ItemStack(Material.BARRIER);
        ItemMeta meta3 = close.getItemMeta();
        meta3.setDisplayName("§cSchließen");
        close.setItemMeta(meta3);
        inv.setItem(8, close);

        p.openInventory(inv);
    }
}
