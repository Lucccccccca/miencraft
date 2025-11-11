package de.luca.plugin;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.*;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class PermsRoleListGUI {

    public static final String TITLE = "§eAlle Rollen";

    private final LucaCrafterPlugin plugin;

    public PermsRoleListGUI(LucaCrafterPlugin plugin) {
        this.plugin = plugin;
    }

    public void open(Player viewer) {
        Inventory inv = Bukkit.createInventory(null, 54, TITLE);

        List<String> roles = plugin.getRoleManager().getAllRoles();
        int i = 0;
        for (String r : roles) {
            inv.setItem(i++, paper(r));
            if (i >= 45) break;
        }

        inv.setItem(49, simple(Material.BARRIER, "§cZurück", null));
        inv.setItem(53, simple(Material.LAVA_BUCKET, "§4Rolle löschen (per Chat)", "§7Klicke und tippe dann den Rollennamen"));

        viewer.openInventory(inv);
    }

    private ItemStack paper(String name) {
        ItemStack it = new ItemStack(Material.PAPER);
        ItemMeta im = it.getItemMeta();
        im.setDisplayName("§f" + name);
        List<String> lore = new ArrayList<>();
        lore.add("§8Klicken zum Öffnen");
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
