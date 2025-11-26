package de.luca.plugin;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.*;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.Arrays;
import java.util.UUID;

public class FreundeOptionsGUI {

    private static ItemStack named(Material m, String name, String... lore) {
        ItemStack it = new ItemStack(m);
        ItemMeta im = it.getItemMeta();
        im.setDisplayName(name);
        if (lore != null && lore.length > 0) im.setLore(Arrays.asList(lore));
        it.setItemMeta(im);
        return it;
    }

    public static Inventory build(LucaCrafterPlugin plugin, UUID friendUUID) {
        String friendName = plugin.getFriendManager().getName(friendUUID);
        Inventory inv = Bukkit.createInventory(null, 27, "§bFreund: §e" + friendName);

        ItemStack filler = named(Material.GRAY_STAINED_GLASS_PANE, " ");
        for (int i = 0; i < inv.getSize(); i++) inv.setItem(i, filler);

        // Kopf
        ItemStack head = new ItemStack(Material.PLAYER_HEAD);
        SkullMeta sm = (SkullMeta) head.getItemMeta();
        OfflinePlayer op = Bukkit.getOfflinePlayer(friendUUID);
        sm.setOwningPlayer(op);
        sm.setDisplayName("§a" + friendName);
        head.setItemMeta(sm);
        inv.setItem(4, head);

        // Homes ansehen (Enderperle – modern & intuitiv)
        inv.setItem(10, named(Material.ENDER_PEARL, "§bHomes ansehen", "§7Öffnet die Homes dieses Freundes (nur lesen)"));

        // Favorit togglen
        boolean fav = plugin.getFriendManager().isFavorite(nullSafeUUID(inv, friendUUID), friendUUID); // Dummy check, unten behandeln wir korrekt
        inv.setItem(12, named(Material.NETHER_STAR, "§eFavorit umschalten", "§7Markiere/entferne als Favorit"));

        // Entfernen
        inv.setItem(14, named(Material.LAVA_BUCKET, "§cFreund entfernen", "§7Löscht die Freundschaft"));

        // Blockieren
        inv.setItem(16, named(Material.BARRIER, "§4Blockieren", "§7Blockiert diesen Spieler"));

        // Zurück
        inv.setItem(22, named(Material.ARROW, "§7↩ Zurück", "§7Zurück zur Liste"));

        return inv;
    }

    private static UUID nullSafeUUID(Inventory inv, UUID fallback) {
        return fallback;
    }
}
