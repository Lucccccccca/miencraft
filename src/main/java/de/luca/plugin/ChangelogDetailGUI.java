package de.luca.plugin;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.*;
import org.bukkit.inventory.meta.ItemMeta;
import java.util.Arrays;

public class ChangelogDetailGUI {

    public static Inventory build(String version, String[] details) {

        Inventory inv = Bukkit.createInventory(null, 54, "§e📘 " + version);

        ItemStack filler = new ItemStack(Material.GRAY_STAINED_GLASS_PANE);
        ItemMeta f = filler.getItemMeta();
        f.setDisplayName(" ");
        filler.setItemMeta(f);

        for (int i = 0; i < inv.getSize(); i++) inv.setItem(i, filler);

        ItemStack book = new ItemStack(Material.WRITABLE_BOOK);
        ItemMeta bm = book.getItemMeta();
        bm.setDisplayName("§bInfos zu " + version);
        bm.setLore(Arrays.asList(details));
        book.setItemMeta(bm);

        inv.setItem(22, book);

        ItemStack back = new ItemStack(Material.ARROW);
        ItemMeta bb = back.getItemMeta();
        bb.setDisplayName("§7← Zurück");
        back.setItemMeta(bb);
        inv.setItem(49, back);

        return inv;
    }
}
