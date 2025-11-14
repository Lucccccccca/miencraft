package de.luca.plugin;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

public class HomePlayerSelectGUI {

    private final Inventory inv;

    public HomePlayerSelectGUI() {
        this.inv = Bukkit.createInventory(null, 54, "§cHomes: Spieler wählen");

        int slot = 0;
        for (Player p : Bukkit.getOnlinePlayers()) {
            if (slot >= inv.getSize()) break;

            ItemStack head = new ItemStack(Material.PLAYER_HEAD);
            SkullMeta meta = (SkullMeta) head.getItemMeta();
            meta.setOwningPlayer(p);
            meta.setDisplayName("§e" + p.getName());
            head.setItemMeta(meta);

            inv.setItem(slot++, head);
        }
    }

    public Inventory getInventory() {
        return inv;
    }
}
