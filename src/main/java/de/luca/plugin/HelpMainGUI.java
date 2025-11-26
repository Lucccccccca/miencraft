package de.luca.plugin;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.*;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class HelpMainGUI {

    private static ItemStack item(Material m, String name, String... lore) {
        ItemStack it = new ItemStack(m);
        ItemMeta im = it.getItemMeta();
        im.setDisplayName(name);
        im.setLore(Arrays.asList(lore));
        it.setItemMeta(im);
        return it;
    }

    public static Inventory build(LucaCrafterPlugin plugin, Player p) {

        Map<HelpCategory, List<String>> map = HelpScanner.scan(plugin);

        Inventory inv = Bukkit.createInventory(null, 54, "§e📘 Hilfe-Menü");

        // Filler
        ItemStack filler = item(Material.BLACK_STAINED_GLASS_PANE, " ");
        for (int i = 0; i < inv.getSize(); i++) inv.setItem(i, filler);

        // Kategorien setzen
        int slot = 10;
        for (HelpCategory cat : HelpCategory.values()) {

            Material icon = getIcon(cat);

            inv.setItem(slot, item(
                    icon,
                    cat.getDisplay(),
                    "§7Klicke, um die Befehle zu sehen",
                    "§8" + map.get(cat).size() + " Befehle"
            ));

            slot++;

            if (slot == 17) slot = 19;
            if (slot == 26) slot = 28;
            if (slot == 35) slot = 37;
            if (slot >= 44) break;
        }

        // 📦 Plugin-Info Button
        inv.setItem(49, item(
                Material.BOOK,
                "§6📦 Plugin-Info",
                "§7Klicke, um Infos zum Plugin zu sehen"
        ));

        return inv;
    }

    private static Material getIcon(HelpCategory cat) {
        return switch (cat) {
            case FREUNDES_SYSTEM -> Material.PLAYER_HEAD;
            case HOME_SYSTEM -> Material.OAK_DOOR;
            case ERZ_BAUM_SYSTEM -> Material.IRON_PICKAXE;
            case SETTINGS -> Material.COMPARATOR;
            case PREFIX_SYSTEM -> Material.NAME_TAG;
            case CRAFTING -> Material.CRAFTING_TABLE;
            case STATS -> Material.BOOK;
            case TELEPORT -> Material.ENDER_PEARL;
            case ADMIN -> Material.REDSTONE_TORCH;
            default -> Material.CHEST;
        };
    }
}
