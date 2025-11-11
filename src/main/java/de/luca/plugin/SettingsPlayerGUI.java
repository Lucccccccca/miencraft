package de.luca.plugin;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class SettingsPlayerGUI {


    public static final String TITLE = "§aSpieler-Einstellungen";

    private final LucaCrafterPlugin plugin;

    public SettingsPlayerGUI(LucaCrafterPlugin plugin) {
        this.plugin = plugin;
    }

    public void open(Player player) {
        Inventory inv = Bukkit.createInventory(null, 27, TITLE);

        boolean magnet = plugin.getConfigManager().isMagnetEnabled(player.getUniqueId());
        boolean pickup = plugin.getConfigManager().isAutoPickupEnabled(player.getUniqueId());
        boolean furnace = plugin.getConfigManager().isFastFurnaceEnabled(player.getUniqueId());

        inv.setItem(10, createItem(Material.COMPASS,
                "§b🧲 Magnet",
                magnet ? "§a✔ Aktiviert" : "§c✖ Deaktiviert"));

        inv.setItem(12, createItem(Material.HOPPER,
                "§e📦 AutoPickup",
                pickup ? "§a✔ Aktiviert" : "§c✖ Deaktiviert"));

        inv.setItem(14, createItem(Material.BLAST_FURNACE,
                "§6🔥 Schneller Ofen",
                furnace ? "§a✔ Aktiviert" : "§c✖ Deaktiviert"));

        inv.setItem(16, createItem(Material.BARRIER, "§c⬅ Zurück", "§7Schließt das Menü"));

        player.openInventory(inv);
    }

    private ItemStack createItem(Material mat, String name, String... lore) {
        ItemStack item = new ItemStack(mat);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(name);
        meta.setLore(java.util.Arrays.asList(lore));
        item.setItemMeta(meta);
        return item;
    }
}
