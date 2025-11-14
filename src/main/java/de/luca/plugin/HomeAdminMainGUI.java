package de.luca.plugin;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Arrays;

public class HomeAdminMainGUI {

    private final Inventory inv;

    public HomeAdminMainGUI(LucaCrafterPlugin plugin) {
        this.inv = Bukkit.createInventory(null, 27, "§cHome-Admin");

        // Füller
        ItemStack filler = new ItemStack(Material.GRAY_STAINED_GLASS_PANE);
        ItemMeta fm = filler.getItemMeta();
        fm.setDisplayName(" ");
        filler.setItemMeta(fm);
        for (int i = 0; i < inv.getSize(); i++) {
            inv.setItem(i, filler);
        }

        // Spieler-Einstellungen
        ItemStack playerSettings = new ItemStack(Material.PLAYER_HEAD);
        ItemMeta pm = playerSettings.getItemMeta();
        pm.setDisplayName("§eSpieler-Einstellungen");
        pm.setLore(Arrays.asList(
                "§7Klicke, um einen Spieler auszuwählen",
                "§7und Home-Einstellungen zu bearbeiten."
        ));
        playerSettings.setItemMeta(pm);
        inv.setItem(11, playerSettings);

        // Hinweis auf /homesadmin <Name> für Homes-Liste
        ItemStack viewHomes = new ItemStack(Material.BOOK);
        ItemMeta vm = viewHomes.getItemMeta();
        vm.setDisplayName("§bSpieler-Homes ansehen");
        vm.setLore(Arrays.asList(
                "§7Nutze:",
                "§f/homesadmin <Spieler>",
                "§7um alle Homes eines Spielers zu sehen."
        ));
        viewHomes.setItemMeta(vm);
        inv.setItem(15, viewHomes);
    }

    public Inventory getInventory() {
        return inv;
    }
}
