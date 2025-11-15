package de.luca.plugin;

import de.luca.plugin.*;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.*;

public class FriendHomesGUI {

    private final Inventory inv;

    public FriendHomesGUI(LucaCrafterPlugin plugin, Player viewer, UUID friend, String friendName) {

        this.inv = Bukkit.createInventory(null, 54, "§aHomes von §e" + friendName);

        ConfigManager cfg = plugin.getConfigManager();
        HomeManager manager = plugin.getHomeManager();

        Map<String, Home> allHomes = manager.getHomes(friend);

        ItemStack filler = new ItemStack(Material.BLACK_STAINED_GLASS_PANE);
        ItemMeta fm = filler.getItemMeta();
        fm.setDisplayName(" ");
        filler.setItemMeta(fm);
        for (int i = 0; i < inv.getSize(); i++) inv.setItem(i, filler);

        int slot = 10;

        for (Home h : allHomes.values()) {

            HomePrivacy privacy = cfg.getHomePrivacy(friend, h.getName());

            boolean allowed =
                    privacy == HomePrivacy.PUBLIC ||
                    (privacy == HomePrivacy.FRIENDS && plugin.getFriendManager().areFriends(viewer.getUniqueId(), friend));

            if (!allowed) continue;

            ItemStack item = new ItemStack(Material.ENDER_PEARL);
            ItemMeta meta = item.getItemMeta();
            meta.setDisplayName("§b" + h.getName());
            meta.setLore(Arrays.asList(
                    "§7Welt: §f" + h.getLocation().getWorld().getName(),
                    "§7X: §f" + h.getLocation().getBlockX(),
                    "§7Y: §f" + h.getLocation().getBlockY(),
                    "§7Z: §f" + h.getLocation().getBlockZ(),
                    "",
                    "§aLinksklick: Teleportieren"
            ));
            item.setItemMeta(meta);

            inv.setItem(slot, item);

            slot++;
            if (slot == 17) slot = 19;
            if (slot == 26) break;
        }
    }

    public Inventory getInventory() {
        return inv;
    }
}
