package de.luca.plugin;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.*;

public class HomeMainGUI {

    private final Inventory inv;

    public HomeMainGUI(LucaCrafterPlugin plugin, Player p) {

        this.inv = Bukkit.createInventory(null, 54, "§aDeine Homes");

        ConfigManager cfg = plugin.getConfigManager();
        HomeManager homeManager = plugin.getHomeManager();

        UUID uuid = p.getUniqueId();

        Map<String, Home> homes = homeManager.getHomes(uuid);

        int maxHomes = cfg.getMaxHomes(uuid);
        int current = homes.size();
        boolean particlesEnabled = cfg.isHomeParticlesEnabled(uuid);

        // ========= FÜLLER =========
        ItemStack filler = new ItemStack(Material.GRAY_STAINED_GLASS_PANE);
        ItemMeta fm = filler.getItemMeta();
        fm.setDisplayName(" ");
        filler.setItemMeta(fm);

        for (int i = 0; i < inv.getSize(); i++) {
            inv.setItem(i, filler);
        }

        // ========= Homes anzeigen =========

        int slot = 10; // Startposition im Inventar

        for (Home h : homes.values()) {

            ItemStack item = new ItemStack(Material.ENDER_PEARL);
            ItemMeta meta = item.getItemMeta();
            meta.setDisplayName("§b" + h.getName());
            meta.setLore(Arrays.asList(
                    "§7Welt: §f" + h.getLocation().getWorld().getName(),
                    "§7X: §f" + h.getLocation().getBlockX(),
                    "§7Y: §f" + h.getLocation().getBlockY(),
                    "§7Z: §f" + h.getLocation().getBlockZ(),
                    "",
                    "§aLinksklick: Teleportieren",
                    "§cRechtsklick: Löschen"
            ));
            item.setItemMeta(meta);

            inv.setItem(slot, item);

            slot++;
            if (slot == 17) slot = 19;
            if (slot == 26) break;
        }

        // ========= Max Homes Anzeige =========
        ItemStack info = new ItemStack(Material.BOOK);
        ItemMeta im = info.getItemMeta();
        im.setDisplayName("§eDein Home-Limit");
        im.setLore(Arrays.asList(
                "§7Aktuell: §a" + current + " §7/ §e" + maxHomes,
                "",
                "§7Mehr Homes kannst du vom Admin erhalten."
        ));
        info.setItemMeta(im);
        inv.setItem(4, info);

        // ========= Partikel Toggle =========
ItemStack particles = new ItemStack(particlesEnabled ? Material.BLAZE_POWDER : Material.COAL);
ItemMeta pm = particles.getItemMeta();
pm.setDisplayName("§bHome-Partikel");
pm.setLore(Arrays.asList(
        "§7Aktuell: " + (particlesEnabled ? "§aAktiv" : "§cDeaktiviert"),
        "",
        "§aKlicke zum Umschalten"
));
particles.setItemMeta(pm);
inv.setItem(48, particles);

// ========= HOLOGRAMM Toggle =========
boolean hologramEnabled = cfg.isHomeHologramEnabled(p.getUniqueId());
ItemStack holo = new ItemStack(hologramEnabled ? Material.ENDER_EYE : Material.ENDER_PEARL);
ItemMeta hm = holo.getItemMeta();
hm.setDisplayName("§bHome-Hologramme");
hm.setLore(Arrays.asList(
        "§7Aktuell: " + (hologramEnabled ? "§aAktiv" : "§cDeaktiviert"),
        "",
        "§aKlicke zum Umschalten"
));
holo.setItemMeta(hm);
inv.setItem(50, holo);

    }

    public Inventory getInventory() {
        return inv;
    }
}
