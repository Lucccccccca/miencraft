package de.luca.plugin;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.SkullType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.*;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.ArrayList;
import java.util.List;

public class PermsMainGUI {

    public static final String TITLE = "§bRollen & Rechte – Spieler wählen";
    private final LucaCrafterPlugin plugin;

    public PermsMainGUI(LucaCrafterPlugin plugin) {
        this.plugin = plugin;
    }

    public void open(Player viewer) {
        Inventory inv = Bukkit.createInventory(null, 54, TITLE);

        // Kopf-Items für ONLINE-Spieler (simple & performant)
        int slot = 0;
        for (Player p : Bukkit.getOnlinePlayers()) {
            if (slot >= 45) break; // 5 Reihen für Spieler, letzte Reihe für Buttons
            inv.setItem(slot++, skullOf(p));
        }

        // Buttons
        inv.setItem(45, simple(Material.WRITABLE_BOOK, "§aNeue Rolle erstellen", "§7Klicke und schreibe den Namen im Chat"));
        inv.setItem(46, simple(Material.ANVIL, "§eRollen verwalten (Liste)", "§7Alle Rollen anzeigen / löschen"));
        inv.setItem(49, simple(Material.BARRIER, "§cSchließen", null));
        inv.setItem(53, simple(Material.CLOCK, "§7Neu laden", "§7Aktualisiert die Spielerliste"));

        viewer.openInventory(inv);
    }

    private ItemStack skullOf(OfflinePlayer p) {
        ItemStack head = new ItemStack(Material.PLAYER_HEAD);
        SkullMeta meta = (SkullMeta) head.getItemMeta();
        meta.setOwningPlayer(p);
        meta.setDisplayName("§f" + p.getName());
        String role = plugin.getRoleManager().getRole(p.getUniqueId());
        List<String> lore = new ArrayList<>();
        lore.add("§7Rolle: §a" + role);
        lore.add("§8» Klicke zum Verwalten");
        meta.setLore(lore);
        head.setItemMeta(meta);
        return head;
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
