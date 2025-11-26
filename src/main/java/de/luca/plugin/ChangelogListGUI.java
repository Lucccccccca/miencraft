package de.luca.plugin;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.*;
import org.bukkit.inventory.meta.ItemMeta;
import java.util.Arrays;

import java.util.LinkedHashMap;

public class ChangelogListGUI {

    // Version -> Beschreibung
    public static LinkedHashMap<String, String[]> CHANGELOG = new LinkedHashMap<>();

    static {
        CHANGELOG.put("v1.2", new String[]{
                "• Neues Home-Menü",
                "• Teleport-Animation",
                "• Viele Fehler behoben"
        });

        CHANGELOG.put("v1.1", new String[]{
                "• Freunde-System überarbeitet",
                "• Favoriten hinzugefügt"
        });

        CHANGELOG.put("v1.0", new String[]{
                "• Erste Version",
                "• Grundfunktionen"
        });
    }

    private static ItemStack item(String version) {
        ItemStack it = new ItemStack(Material.PAPER);
        ItemMeta im = it.getItemMeta();
        im.setDisplayName("§a" + version);
        it.setItemMeta(im);
        return it;
    }

    public static Inventory build(Player p) {

        Inventory inv = Bukkit.createInventory(null, 54, "§e📝 Changelog");

        ItemStack filler = new ItemStack(Material.GRAY_STAINED_GLASS_PANE);
        ItemMeta f = filler.getItemMeta();
        f.setDisplayName(" ");
        filler.setItemMeta(f);

        for (int i = 0; i < inv.getSize(); i++) inv.setItem(i, filler);

        int slot = 10;

        for (String version : CHANGELOG.keySet()) {
            inv.setItem(slot, item(version));
            slot++;
            if (slot == 17) slot = 19;
            if (slot == 26) slot = 28;
            if (slot == 35) slot = 37;
        }

        ItemStack back = new ItemStack(Material.ARROW);
        ItemMeta bm = back.getItemMeta();
        bm.setDisplayName("§7← Zurück");
        back.setItemMeta(bm);
        inv.setItem(49, back);

        return inv;
    }
}
