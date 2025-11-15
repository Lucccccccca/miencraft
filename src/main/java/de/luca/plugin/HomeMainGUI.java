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
        Map<String, Home> homeMap = homeManager.getHomes(uuid);

        int maxHomes = cfg.getMaxHomes(uuid);
        int current = homeMap.size();
        boolean particlesEnabled = cfg.isHomeParticlesEnabled(uuid);
        boolean hologramEnabled = cfg.isHomeHologramEnabled(uuid);
        HomeSortMode sortMode = cfg.getHomeSortMode(uuid);

        // Füller
        ItemStack filler = new ItemStack(Material.GRAY_STAINED_GLASS_PANE);
        ItemMeta fm = filler.getItemMeta();
        fm.setDisplayName(" ");
        filler.setItemMeta(fm);
        for (int i = 0; i < inv.getSize(); i++) {
            inv.setItem(i, filler);
        }

        // Homes in Liste + Sortierung anwenden
        List<Home> homes = new ArrayList<>(homeMap.values());
        switch (sortMode) {
            case DISTANCE:
                homes.sort(Comparator.comparingDouble(h -> h.getLocation().distance(p.getLocation())));
                break;
            case WORLD:
                homes.sort(Comparator
                        .comparing((Home h) -> h.getLocation().getWorld() != null
                                ? h.getLocation().getWorld().getName()
                                : "zzz")
                        .thenComparing(h -> h.getName().toLowerCase())
                );
                break;
            case NAME:
            default:
                homes.sort(Comparator.comparing(h -> h.getName().toLowerCase()));
                break;
        }

        // Homes anzeigen
        int slot = 10;
        for (Home h : homes) {

            String name = h.getName();
            String world = h.getLocation().getWorld() != null
                    ? h.getLocation().getWorld().getName()
                    : "Unbekannt";

            HomePrivacy privacy = cfg.getHomePrivacy(uuid, name);

            ItemStack item = new ItemStack(Material.ENDER_PEARL);
            ItemMeta meta = item.getItemMeta();
            meta.setDisplayName("§b" + name);
            List<String> lore = new ArrayList<>();
            lore.add("§7Welt: §f" + world);
            lore.add("§7X: §f" + h.getLocation().getBlockX()
                    + " §7Y: §f" + h.getLocation().getBlockY()
                    + " §7Z: §f" + h.getLocation().getBlockZ());
            lore.add("");
            lore.add("§7Zugriff: " + privacy.getDisplayName());
            lore.add("");
            lore.add("§aLinksklick: Teleportieren");
            lore.add("§cRechtsklick: Löschen");
            lore.add("§eShift + Rechtsklick: Privatsphäre ändern");
            meta.setLore(lore);
            item.setItemMeta(meta);

            inv.setItem(slot, item);

            slot++;
            if (slot == 17) slot = 19;
            if (slot == 26) break;
        }

        // Info: Max Homes
        ItemStack info = new ItemStack(Material.BOOK);
        ItemMeta im = info.getItemMeta();
        im.setDisplayName("§eDein Home-Limit");
        im.setLore(Arrays.asList(
                "§7Aktuell: §a" + current + " §7/ §e" + maxHomes,
                "",
                "§7Mehr Homes kann dir ein Admin geben."
        ));
        info.setItemMeta(im);
        inv.setItem(4, info);

        // Partikel-Toggle
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

        // Hologramm-Toggle
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

        // Sortier-Modus
        ItemStack sortItem = new ItemStack(Material.COMPARATOR);
        ItemMeta sm = sortItem.getItemMeta();
        sm.setDisplayName("§bSortierung");
        sm.setLore(Arrays.asList(
                "§7Aktuell: §e" + getSortName(sortMode),
                "",
                "§aKlicke zum Wechseln"
        ));
        sortItem.setItemMeta(sm);
        inv.setItem(46, sortItem);
    }

    private String getSortName(HomeSortMode mode) {
        switch (mode) {
            case DISTANCE:
                return "Entfernung";
            case WORLD:
                return "Welt";
            case NAME:
            default:
                return "Name";
        }
    }

    public Inventory getInventory() {
        return inv;
    }
}
