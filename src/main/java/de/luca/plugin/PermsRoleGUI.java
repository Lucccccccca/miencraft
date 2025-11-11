package de.luca.plugin;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.*;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.*;

public class PermsRoleGUI {

    public static final String TITLE_PREFIX = "§9Rolle: ";
    private final LucaCrafterPlugin plugin;
    private final String role;

    // Bekannte Perm-Knoten deines Plugins (erweiterbar)
    public static final List<String> KNOWN_NODES = List.of(
            // Quality-of-Life
            "lucacrafter.autopickup",
            "lucacrafter.magnet",
            "lucacrafter.fastfurnace",
            // Systeme / Befehle
            "lucacrafter.baum.use",
            "lucacrafter.erz.use",
            "lucacrafter.stats.use",
            "lucacrafter.afk.use",
            "lucacrafter.craftgui.use",
            "lucacrafter.settings.open",
            // Admin
            "lucacrafter.antigrief.toggle",
            "lucacrafter.perms.manage"
    );

    public PermsRoleGUI(LucaCrafterPlugin plugin, String role) {
        this.plugin = plugin;
        this.role = role;
    }

    public void open(Player viewer) {
        Inventory inv = Bukkit.createInventory(null, 54, TITLE_PREFIX + role);

        Set<String> enabled = plugin.getRoleManager().getRolePermissions(role);

        int i = 0;
        for (String node : KNOWN_NODES) {
            boolean on = enabled.contains(node);
            inv.setItem(i++, toggleItem(node, on));
            if (i >= 45) break; // unten Buttons
        }

        inv.setItem(49, simple(Material.BARRIER, "§cZurück", null));
        inv.setItem(53, simple(Material.EMERALD_BLOCK, "§aSpeichern", "§7Aktuelle Toggles sind schon live"));
        viewer.openInventory(inv);
    }

    private ItemStack toggleItem(String node, boolean on) {
        ItemStack it = new ItemStack(on ? Material.LIME_WOOL : Material.RED_WOOL);
        ItemMeta im = it.getItemMeta();
        im.setDisplayName((on ? "§a" : "§c") + node);
        List<String> lore = new ArrayList<>();
        lore.add(on ? "§7Aktuell: §aerlaubt" : "§7Aktuell: §cverboten");
        lore.add("§8Klicken zum Umschalten");
        im.setLore(lore);
        it.setItemMeta(im);
        return it;
    }

    private ItemStack simple(Material m, String name, String loreLine) {
        ItemStack it = new ItemStack(m);
        ItemMeta im = it.getItemMeta();
        im.setDisplayName(name);
        if (loreLine != null) {
            List<String> lore = new ArrayList<>();
            lore.add(loreLine);
            im.setLore(lore);
        }
        it.setItemMeta(im);
        return it;
    }
}
