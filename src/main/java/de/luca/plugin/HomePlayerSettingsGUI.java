package de.luca.plugin;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Arrays;
import java.util.UUID;

public class HomePlayerSettingsGUI {

    private final Inventory inv;

    public HomePlayerSettingsGUI(LucaCrafterPlugin plugin, OfflinePlayer target) {
        String name = target.getName() == null ? "Unbekannt" : target.getName();
        this.inv = Bukkit.createInventory(null, 27, "§cHome-Settings: " + name);

        ConfigManager config = plugin.getConfigManager();
        UUID uuid = target.getUniqueId();

        int maxHomes = config.getMaxHomes(uuid);
        int cooldown = config.getHomeCooldown(uuid);
        int delay = config.getHomeTeleportDelay(uuid);
        boolean instant = config.isHomeInstantTeleport(uuid);
        boolean moveCancel = config.isHomeMoveCancelEnabled(uuid);
        boolean particles = config.isHomeParticlesEnabled(uuid);

        // Füller
        ItemStack filler = new ItemStack(Material.GRAY_STAINED_GLASS_PANE);
        ItemMeta fm = filler.getItemMeta();
        fm.setDisplayName(" ");
        filler.setItemMeta(fm);
        for (int i = 0; i < inv.getSize(); i++) {
            inv.setItem(i, filler);
        }

        // Max Homes
        inv.setItem(10, createItem(
                Material.NETHER_STAR,
                "§bMax. Homes",
                "§7Aktuell: §e" + maxHomes,
                "",
                "§aLinksklick: +1",
                "§cRechtsklick: -1"
        ));

        // Cooldown
        inv.setItem(12, createItem(
                Material.CLOCK,
                "§bCooldown (Sekunden)",
                "§7Aktuell: §e" + cooldown,
                "",
                "§aLinksklick: +1",
                "§cRechtsklick: -1"
        ));

        // Delay
        inv.setItem(14, createItem(
                Material.REDSTONE_LAMP,
                "§bDelay bis Teleport (Sekunden)",
                "§7Aktuell: §e" + delay,
                "",
                "§aLinksklick: +1",
                "§cRechtsklick: -1"
        ));

        // Instant-Teleport
        inv.setItem(16, createItem(
                instant ? Material.EMERALD_BLOCK : Material.REDSTONE_BLOCK,
                "§bInstant-Teleport",
                "§7Aktuell: " + (instant ? "§aAktiv" : "§cDeaktiviert"),
                "",
                "§aKlicke zum Umschalten"
        ));

        // Move-Cancel
        inv.setItem(19, createItem(
                moveCancel ? Material.FEATHER : Material.ANVIL,
                "§bBewegung bricht Teleport ab",
                "§7Aktuell: " + (moveCancel ? "§aAktiv" : "§cDeaktiviert"),
                "",
                "§aKlicke zum Umschalten"
        ));

        // Partikel
        inv.setItem(21, createItem(
                particles ? Material.BLAZE_POWDER : Material.GUNPOWDER,
                "§bHome-Partikel",
                "§7Aktuell: " + (particles ? "§aAktiv" : "§cDeaktiviert"),
                "",
                "§aKlicke zum Umschalten"
        ));
    }

    private ItemStack createItem(Material mat, String name, String... lore) {
        ItemStack item = new ItemStack(mat);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(name);
        meta.setLore(Arrays.asList(lore));
        item.setItemMeta(meta);
        return item;
    }

    public Inventory getInventory() {
        return inv;
    }
}
