package de.luca.plugin;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.*;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class HelpCategoryGUI {

    private static ItemStack item(Material m, String name, String... lore) {
        ItemStack it = new ItemStack(m);
        ItemMeta im = it.getItemMeta();
        im.setDisplayName(name);
        im.setLore(Arrays.asList(lore));
        it.setItemMeta(im);
        return it;
    }

    public static Inventory build(LucaCrafterPlugin plugin, HelpCategory category, Player p) {

        Map<HelpCategory, List<String>> map = HelpScanner.scan(plugin);
        List<String> cmds = map.get(category);

        Inventory inv = Bukkit.createInventory(null, 54, category.getDisplay());

        ItemStack filler = item(Material.BLACK_STAINED_GLASS_PANE, " ");
        for (int i = 0; i < inv.getSize(); i++) inv.setItem(i, filler);

        int slot = 10;

        for (String cmd : cmds) {

            inv.setItem(slot, item(
                    Material.PAPER,
                    "§a/" + cmd,
                    "§7Befehl zu: " + category.getDisplay(),
                    "§fKlicke für mehr Infos (optional)"
            ));

            slot++;
            if (slot == 17) slot = 19;
            if (slot == 26) slot = 28;
            if (slot == 35) slot = 37;
            if (slot >= 44) break;
        }

        inv.setItem(49, item(Material.ARROW, "§7← Zurück"));

        return inv;
    }
}
