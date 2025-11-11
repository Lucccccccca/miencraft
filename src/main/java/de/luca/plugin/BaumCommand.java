package de.luca.plugin;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

/**
 * Kommando für das Baumfäller-Menü.
 * Öffnet ein GUI, in dem der Spieler Einstellungen wie Automatisches Fällen und Haltbarkeitsfaktor ändern kann.
 * Nach dem Öffnen bekommt der Spieler eine Chat-Meldung als Bestätigung.
 */
public class BaumCommand implements CommandExecutor, Listener {

    private final LucaCrafterPlugin plugin;
    private final ConfigManager cfg;

    public BaumCommand(LucaCrafterPlugin plugin) {
        this.plugin = plugin;
        this.cfg = plugin.getConfigManager();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player)) return true;
        Player p = (Player) sender;
        openMenu(p);
        // Chat-Bestätigung
        p.sendMessage(ChatColor.GREEN + "🌲 Das Baumfäller-Menü wurde geöffnet.");
        return true;
    }

    private void openMenu(Player p) {
        boolean enabled = cfg.getBaumEnabled(p.getUniqueId());
        double factor = cfg.getBaumDurabilityFactor(p.getUniqueId());
        boolean sneak = cfg.getBaumRequireSneak(p.getUniqueId());
        boolean replant = cfg.getBaumAutoReplant(p.getUniqueId());

        Inventory inv = Bukkit.createInventory(null, 9, ChatColor.GREEN + "🌲 Baumfäller Menü");

        // 2 – Ein/Aus
        inv.setItem(2, make(enabled ? Material.LIME_WOOL : Material.RED_WOOL,
                enabled ? ChatColor.GREEN + "Baumfäller: AN" : ChatColor.RED + "Baumfäller: AUS"));

        // 4 – Haltbarkeits-Faktor
        inv.setItem(4, make(Material.ANVIL,
                ChatColor.YELLOW + "Haltbarkeits-Faktor: " + ChatColor.WHITE + "x" + factor));

        // 6 – Sneaken nötig
        inv.setItem(6, make(sneak ? Material.LEATHER_BOOTS : Material.IRON_BOOTS,
                ChatColor.AQUA + "Sneaken nötig: " + (sneak ? ChatColor.GREEN + "JA" : ChatColor.RED + "NEIN")));

        // 7 – Auto-Replant
        inv.setItem(7, make(replant ? Material.OAK_SAPLING : Material.DEAD_BUSH,
                ChatColor.LIGHT_PURPLE + "Auto-Replant: " + (replant ? ChatColor.GREEN + "AN" : ChatColor.RED + "AUS")));

        p.openInventory(inv);
    }

    private ItemStack make(Material mat, String name) {
        ItemStack it = new ItemStack(mat);
        ItemMeta meta = it.getItemMeta();
        meta.setDisplayName(name);
        it.setItemMeta(meta);
        return it;
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent e) {
        if (!e.getView().getTitle().equals(ChatColor.GREEN + "🌲 Baumfäller Menü")) return;
        e.setCancelled(true);
        if (!(e.getWhoClicked() instanceof Player)) return;
        Player p = (Player) e.getWhoClicked();

        int slot = e.getSlot();
        boolean enabled = cfg.getBaumEnabled(p.getUniqueId());
        double factor = cfg.getBaumDurabilityFactor(p.getUniqueId());
        boolean sneak = cfg.getBaumRequireSneak(p.getUniqueId());
        boolean replant = cfg.getBaumAutoReplant(p.getUniqueId());

        switch (slot) {
            case 2 -> {
                cfg.setBaumEnabled(p.getUniqueId(), !enabled);
                p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1f, 1f);
            }
            case 4 -> {
                double next = (factor == 0.5) ? 1.0 : (factor == 1.0) ? 2.0 : 0.5;
                cfg.setBaumDurabilityFactor(p.getUniqueId(), next);
                p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1f, 1f);
            }
            case 6 -> {
                cfg.setBaumRequireSneak(p.getUniqueId(), !sneak);
                p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1f, 1f);
            }
            case 7 -> {
                cfg.setBaumAutoReplant(p.getUniqueId(), !replant);
                p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1f, 1f);
            }
            default -> {
                return;
            }
        }

        // Menü neu öffnen, damit der Spieler den aktualisierten Status sieht
        Bukkit.getScheduler().runTaskLater(plugin, () -> openMenu(p), 2L);
    }
}
