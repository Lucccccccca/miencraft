package de.luca.plugin;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.*;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Arrays;
import java.util.List;

public class PluginInfoGUI {

    private static ItemStack item(Material m, String name, String... lore) {
        ItemStack it = new ItemStack(m);
        ItemMeta im = it.getItemMeta();
        im.setDisplayName(name);
        im.setLore(Arrays.asList(lore));
        it.setItemMeta(im);
        return it;
    }

    public static Inventory build(LucaCrafterPlugin plugin, Player p) {

        Inventory inv = Bukkit.createInventory(null, 27, "§e📦 Plugin-Info");

        ItemStack filler = new ItemStack(Material.GRAY_STAINED_GLASS_PANE);
        ItemMeta fm = filler.getItemMeta();
        fm.setDisplayName(" ");
        filler.setItemMeta(fm);

        for (int i = 0; i < inv.getSize(); i++) inv.setItem(i, filler);

        String name = plugin.getDescription().getName();
        String version = plugin.getDescription().getVersion();
        List<String> authors = plugin.getDescription().getAuthors();

        inv.setItem(10, item(Material.PLAYER_HEAD, "§b👨‍💻 Entwickler",
                "§7" + authors));

        inv.setItem(11, item(Material.BOOK, "§b🧩 Plugin-Name",
                "§a" + name));

        inv.setItem(12, item(Material.PAPER, "§b🔢 Version",
                "§a" + version));

        inv.setItem(14, item(Material.WRITABLE_BOOK, "§b📝 Changelog",
                "§7Klicke, um alle Updates zu sehen"));

        inv.setItem(15, item(Material.COMMAND_BLOCK, "§b🌐 GitHub",
                "§9TeSt"));

        inv.setItem(16, item(Material.DIAMOND, "§b💬 Discord",
                "§9luca25018"));

        ItemStack back = new ItemStack(Material.ARROW);
        ItemMeta bm = back.getItemMeta();
        bm.setDisplayName("§7← Zurück");
        back.setItemMeta(bm);
        inv.setItem(22, back);

        return inv;
    }
}
