package de.luca.plugin;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.*;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Arrays;

public class FreundeAddGUI {

    private static ItemStack named(Material m, String name, String... lore) {
        ItemStack it = new ItemStack(m);
        ItemMeta im = it.getItemMeta();
        im.setDisplayName(name);
        if (lore != null && lore.length > 0) im.setLore(Arrays.asList(lore));
        it.setItemMeta(im);
        return it;
    }

    public static Inventory build(Player p) {
        Inventory inv = Bukkit.createInventory(null, 27, "§aFreund hinzufügen");

        ItemStack filler = named(Material.BLACK_STAINED_GLASS_PANE, " ");
        for (int i = 0; i < inv.getSize(); i++) inv.setItem(i, filler);

        inv.setItem(11, named(Material.PLAYER_HEAD, "§a👤 Spieler online", "§7Zeigt Köpfe aller online Spieler", "§7Linksklick: Anfrage senden"));
        inv.setItem(15, named(Material.OAK_SIGN, "§e⌨ Manuell eingeben", "§7Gib den Namen im Chat ein", "§7Schreibe §c'abbrechen' §7zum Stoppen"));
        inv.setItem(22, named(Material.ARROW, "§7↩ Zurück", "§7Zurück zum Freunde-Menü"));

        return inv;
    }
}
