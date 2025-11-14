package de.luca.plugin;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Map;

public class HomeMainGUI {

    private final Inventory inv;
    private final Player player;
    private final LucaCrafterPlugin plugin;

    public HomeMainGUI(LucaCrafterPlugin plugin, Player player) {
        this.plugin = plugin;
        this.player = player;

        this.inv = Bukkit.createInventory(null, 54, "§aDeine Homes");
        load();
    }

    public Inventory getInventory() {
        return inv;
    }

    private void load() {

        inv.clear();
        Map<String, Home> homes = plugin.getHomeManager().getHomes(player.getUniqueId());

        int slot = 0;
        for (Home h : homes.values()) {
            ItemStack item = new ItemStack(Material.ENDER_PEARL);
            ItemMeta meta = item.getItemMeta();
            meta.setDisplayName("§b" + h.getName());
            meta.setLore(java.util.Arrays.asList(
                    "§7Klicke, um dich zu teleportieren.",
                    "",
                    "§8X: " + h.getLocation().getBlockX(),
                    "§8Y: " + h.getLocation().getBlockY(),
                    "§8Z: " + h.getLocation().getBlockZ()
            ));
            item.setItemMeta(meta);

            inv.setItem(slot++, item);
        }

        // Einstellungen-Button
        ItemStack settings = new ItemStack(Material.COMPARATOR);
        ItemMeta m = settings.getItemMeta();
        m.setDisplayName("§eHome-Einstellungen");
        settings.setItemMeta(m);

        inv.setItem(53, settings);
    }
}
