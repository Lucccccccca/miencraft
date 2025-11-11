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

import java.util.Map;

public class StatsCommand implements CommandExecutor, Listener {

    private final LucaCrafterPlugin plugin;

    public StatsCommand(LucaCrafterPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player)) return true;
        Player p = (Player) sender;
        openMainMenu(p);
        return true;
    }

    // 📊 Hauptmenü
    private void openMainMenu(Player p) {
        Inventory inv = Bukkit.createInventory(null, 9, ChatColor.GOLD + "📊 Statistik");

        inv.setItem(3, make(Material.OAK_LOG, ChatColor.GREEN + "🌲 Baum-Statistik", ChatColor.GRAY + "Klicke zum Anzeigen"));
        inv.setItem(5, make(Material.DIAMOND_ORE, ChatColor.AQUA + "⛏ Erz-Statistik", ChatColor.GRAY + "Klicke zum Anzeigen"));

        p.openInventory(inv);
        p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1f, 1.2f);
    }

    // 🌲 Baum-Statistik
    private void openTreeStats(Player p) {
        Map<String, Integer> treeStats = plugin.getConfigManager().getTreeStats(p.getUniqueId());
        Inventory inv = Bukkit.createInventory(null, 54, ChatColor.GREEN + "🌲 Gefällte Bäume");

        int slot = 0;
        for (Map.Entry<String, Integer> entry : treeStats.entrySet()) {
            if (slot >= 54) break;
            try {
                Material mat = Material.valueOf(entry.getKey());
                int amount = entry.getValue();
                inv.setItem(slot++, make(mat, ChatColor.YELLOW + formatName(entry.getKey()), ChatColor.GRAY + "Anzahl: " + ChatColor.WHITE + amount));
            } catch (Exception ignored) {}
        }

        inv.setItem(53, make(Material.ARROW, ChatColor.RED + "← Zurück", ""));
        p.openInventory(inv);
    }

    // ⛏ Erz-Statistik
    private void openOreStats(Player p) {
        Map<String, Integer> oreStats = plugin.getConfigManager().getOreStats(p.getUniqueId());
        Inventory inv = Bukkit.createInventory(null, 54, ChatColor.AQUA + "⛏ Abgebaute Erze");

        int slot = 0;
        for (Map.Entry<String, Integer> entry : oreStats.entrySet()) {
            if (slot >= 54) break;
            try {
                Material mat = Material.valueOf(entry.getKey());
                int amount = entry.getValue();
                inv.setItem(slot++, make(mat, ChatColor.YELLOW + formatName(entry.getKey()), ChatColor.GRAY + "Anzahl: " + ChatColor.WHITE + amount));
            } catch (Exception ignored) {}
        }

        inv.setItem(53, make(Material.ARROW, ChatColor.RED + "← Zurück", ""));
        p.openInventory(inv);
    }

    // 🧱 Item-Helfer
    private ItemStack make(Material mat, String name, String loreLine) {
        if (mat == null || mat == Material.AIR) mat = Material.BARRIER;

        ItemStack it = new ItemStack(mat);
        ItemMeta meta = it.getItemMeta();

        if (meta == null) {
            Bukkit.getLogger().warning("⚠️ ItemMeta ist null für Material: " + mat.name());
            return it;
        }

        meta.setDisplayName(name);
        if (loreLine != null && !loreLine.isEmpty()) {
            meta.setLore(java.util.List.of(loreLine));
        }

        it.setItemMeta(meta);
        return it;
    }

    private String formatName(String key) {
        return key.toLowerCase()
                .replace("_log", "")
                .replace("_ore", "")
                .replace("_", " ")
                .replace("minecraft:", "")
                .trim();
    }

    // 🖱 Klick-Events
    @EventHandler
    public void onClick(InventoryClickEvent e) {
        if (!(e.getWhoClicked() instanceof Player p)) return;

        String title = ChatColor.stripColor(e.getView().getTitle());
        ItemStack clickedItem = e.getCurrentItem();

        if (title == null) return;
        if (!title.contains("Statistik") && !title.contains("Gefällte Bäume") && !title.contains("Abgebaute Erze")) return;

        e.setCancelled(true);

        if (title.contains("Statistik")) {
            if (clickedItem == null || clickedItem.getType() == Material.AIR) return;

            if (clickedItem.getType() == Material.OAK_LOG) {
                openTreeStats(p);
                p.playSound(p.getLocation(), Sound.UI_BUTTON_CLICK, 1f, 1.3f);
            } else if (clickedItem.getItemMeta() != null &&
                    ChatColor.stripColor(clickedItem.getItemMeta().getDisplayName()).contains("Erz")) {
                openOreStats(p);
                p.playSound(p.getLocation(), Sound.UI_BUTTON_CLICK, 1f, 1.3f);
            }
            return;
        }

        if (title.contains("Gefällte Bäume") || title.contains("Abgebaute Erze")) {
            if (clickedItem != null && clickedItem.getType() == Material.ARROW) {
                openMainMenu(p);
                p.playSound(p.getLocation(), Sound.UI_BUTTON_CLICK, 1f, 1.0f);
            }
        }
    }
}
