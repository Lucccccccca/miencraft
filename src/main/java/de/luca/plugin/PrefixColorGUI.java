package de.luca.plugin;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import static de.luca.plugin.PrefixGUI.createItem;

public class PrefixColorGUI {

    public static final String TITLE = "§aPrefix-Farbe";

    public static void open(Player player) {
        Inventory inv = Bukkit.createInventory(null, 27, TITLE);

        ItemStack glass = createItem(Material.GRAY_STAINED_GLASS_PANE, " ");
        for (int i = 0; i < inv.getSize(); i++) {
            inv.setItem(i, glass);
        }

        inv.setItem(10, createItem(Material.WHITE_WOOL, "§fWeiß"));
        inv.setItem(11, createItem(Material.LIGHT_GRAY_WOOL, "§7Grau"));
        inv.setItem(12, createItem(Material.YELLOW_WOOL, "§eGelb"));
        inv.setItem(13, createItem(Material.GREEN_WOOL, "§aGrün"));
        inv.setItem(14, createItem(Material.LIGHT_BLUE_WOOL, "§bHellblau"));
        inv.setItem(15, createItem(Material.BLUE_WOOL, "§9Blau"));
        inv.setItem(16, createItem(Material.RED_WOOL, "§cRot"));

        inv.setItem(22, createItem(Material.BARRIER, "§cZurück"));

        player.openInventory(inv);
    }
}
