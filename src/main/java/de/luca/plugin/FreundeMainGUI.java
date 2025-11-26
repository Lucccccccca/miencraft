package de.luca.plugin;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.*;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Arrays;

public class FreundeMainGUI {

    private static ItemStack named(Material m, String name, String... lore) {
        ItemStack it = new ItemStack(m);
        ItemMeta im = it.getItemMeta();
        im.setDisplayName(name);
        if (lore != null && lore.length > 0) im.setLore(Arrays.asList(lore));
        it.setItemMeta(im);
        return it;
    }

    public static Inventory build(LucaCrafterPlugin plugin, Player p) {
        Inventory inv = Bukkit.createInventory(null, 27, "§bFreunde-Menü");

        ItemStack filler = named(Material.GRAY_STAINED_GLASS_PANE, " ");
        for (int i = 0; i < inv.getSize(); i++) inv.setItem(i, filler);

        inv.setItem(10, named(Material.PLAYER_HEAD, "§a👥 Freunde (Liste)", "§7Zeigt deine Freunde (online/offline)"));
        inv.setItem(12, named(Material.LIME_DYE, "§a🟢 Freunde Online", "§7Zeigt alle Freunde, die gerade online sind"));
        inv.setItem(14, named(Material.RED_DYE, "§c🔴 Freunde Offline", "§7Zeigt alle Freunde, die offline sind"));
        inv.setItem(16, named(Material.NETHER_STAR, "§e⭐ Favoriten", "§7Deine favorisierten Freunde"));
        inv.setItem(22, named(Material.BARRIER, "§4⛔ Blockierte", "§7Spieler, die du blockiert hast"));

        // Dein Wunsch: „Freund hinzufügen (+)“
        inv.setItem(13, named(Material.EMERALD, "§a➕ Freund hinzufügen (+)", "§7Wähle:", "§7• Spieler online (mit Köpfen)", "§7• Manuell eingeben (Chat)"));

        return inv;
    }
}
