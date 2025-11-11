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

import java.util.*;

public class ErzCommand implements CommandExecutor, Listener {

    private final LucaCrafterPlugin plugin;

    public ErzCommand(LucaCrafterPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player p)) return true;

        openErzMenu(p);
        return true;
    }

    // 📊 Hauptmenü für Erz-Einstellungen
    private void openErzMenu(Player p) {
        Inventory inv = Bukkit.createInventory(null, 27, ChatColor.AQUA + "⛏ Erz-System");

        boolean enabled = plugin.getConfigManager().getErzEnabled(p.getUniqueId());
        boolean sneak = plugin.getConfigManager().getErzRequireSneak(p.getUniqueId());
        boolean onlyStandard = plugin.getConfigManager().getErzOnlyStandard(p.getUniqueId());
        double factor = plugin.getConfigManager().getErzDurabilityFactor(p.getUniqueId());

        inv.setItem(10, makeToggle(Material.REDSTONE_ORE, ChatColor.GREEN + "Erz-Erkennung", enabled, "Automatischer Erzabbau"));
        inv.setItem(12, makeToggle(Material.IRON_PICKAXE, ChatColor.GOLD + "Sneaken nötig", sneak, "Nur beim Schleichen aktiv"));
        inv.setItem(14, makeToggle(Material.COAL_ORE, ChatColor.YELLOW + "Nur Standard-Erze", onlyStandard, "Ignoriert Deepslate & Nether"));
        inv.setItem(16, make(Material.ANVIL, ChatColor.AQUA + "Haltbarkeitsfaktor: " + ChatColor.WHITE + factor, ChatColor.GRAY + "Beeinflusst Tool-Verschleiß"));

        inv.setItem(22, make(Material.BOOK, ChatColor.LIGHT_PURPLE + "📊 Erz-Statistik", ChatColor.GRAY + "Zeigt deine abgebauten Erze"));
        p.openInventory(inv);
        p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1f, 1.2f);
    }

    // 📈 Statistik-GUI
    private void openStats(Player p) {
        Map<String, Integer> rawData = plugin.getConfigManager().getOreStats(p.getUniqueId());

        // Werte zusammenfassen (DEEPSLATE_ + Standard)
        Map<String, Integer> merged = new HashMap<>();
        for (Map.Entry<String, Integer> entry : rawData.entrySet()) {
            String key = entry.getKey();
            String normalized = key.replace("DEEPSLATE_", "");
            merged.put(normalized, merged.getOrDefault(normalized, 0) + entry.getValue());
        }

        // Sortierung nach Menge
        List<Map.Entry<String, Integer>> sorted = new ArrayList<>(merged.entrySet());
        sorted.sort((a, b) -> b.getValue() - a.getValue());

        Inventory inv = Bukkit.createInventory(null, 54, ChatColor.AQUA + "⛏ Abgebaute Erze");

        int slot = 0;
        for (Map.Entry<String, Integer> entry : sorted) {
            if (slot >= 53) break;
            try {
                Material mat = Material.valueOf(entry.getKey());
                inv.setItem(slot++, make(mat,
                        ChatColor.YELLOW + formatName(entry.getKey()),
                        ChatColor.GRAY + "Anzahl: " + ChatColor.WHITE + entry.getValue()));
            } catch (Exception ignored) {}
        }

        inv.setItem(53, make(Material.ARROW, ChatColor.RED + "← Zurück", ""));
        p.openInventory(inv);
        p.playSound(p.getLocation(), Sound.UI_BUTTON_CLICK, 1f, 1.3f);
    }

    // 🧱 Item-Erstellung mit Status oder Beschreibung
    private ItemStack make(Material mat, String name, String lore) {
        ItemStack it = new ItemStack(mat);
        ItemMeta meta = it.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(name);
            if (lore != null && !lore.isEmpty()) {
                meta.setLore(List.of(ChatColor.GRAY + lore));
            }
            it.setItemMeta(meta);
        }
        return it;
    }

    private ItemStack makeToggle(Material mat, String name, boolean active, String desc) {
        ItemStack it = new ItemStack(mat);
        ItemMeta meta = it.getItemMeta();
        if (meta != null) {
            meta.setDisplayName((active ? ChatColor.GREEN : ChatColor.RED) + name);
            meta.setLore(List.of(
                    ChatColor.GRAY + desc,
                    ChatColor.DARK_GRAY + "Status: " + (active ? ChatColor.GREEN + "Aktiv" : ChatColor.RED + "Deaktiviert")
            ));
            it.setItemMeta(meta);
        }
        return it;
    }

    private String formatName(String key) {
        return key.toLowerCase()
                .replace("_ore", "")
                .replace("_", " ")
                .replace("deepslate", "(Deepslate) ")
                .trim();
    }

    // 🖱 Klick-Event-Handler
    @EventHandler
    public void onClick(InventoryClickEvent e) {
        if (!(e.getWhoClicked() instanceof Player p)) return;

        String title = ChatColor.stripColor(e.getView().getTitle());
        if (title == null) return;

        // Normales Inventar -> ignorieren
        if (!title.contains("Erz-System") && !title.contains("Abgebaute Erze")) return;
        e.setCancelled(true);

        ItemStack clicked = e.getCurrentItem();
        if (clicked == null || !clicked.hasItemMeta()) return;

        String display = ChatColor.stripColor(clicked.getItemMeta().getDisplayName());

        // Hauptmenü
        if (title.contains("Erz-System")) {
            switch (display) {
                case "Erz-Erkennung" -> {
                    boolean newState = !plugin.getConfigManager().getErzEnabled(p.getUniqueId());
                    plugin.getConfigManager().setErzEnabled(p.getUniqueId(), newState);
                    p.playSound(p.getLocation(), Sound.UI_BUTTON_CLICK, 1f, 1.2f);
                    openErzMenu(p);
                }
                case "Sneaken nötig" -> {
                    boolean newState = !plugin.getConfigManager().getErzRequireSneak(p.getUniqueId());
                    plugin.getConfigManager().setErzRequireSneak(p.getUniqueId(), newState);
                    p.playSound(p.getLocation(), Sound.UI_BUTTON_CLICK, 1f, 1.2f);
                    openErzMenu(p);
                }
                case "Nur Standard-Erze" -> {
                    boolean newState = !plugin.getConfigManager().getErzOnlyStandard(p.getUniqueId());
                    plugin.getConfigManager().setErzOnlyStandard(p.getUniqueId(), newState);
                    p.playSound(p.getLocation(), Sound.UI_BUTTON_CLICK, 1f, 1.2f);
                    openErzMenu(p);
                }
                case "Haltbarkeitsfaktor:" -> {
                    p.sendMessage(ChatColor.YELLOW + "⚙️ Dieser Wert kann aktuell nur manuell in der Config geändert werden.");
                    p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_BASS, 1f, 0.5f);
                }
                case "📊 Erz-Statistik" -> openStats(p);
            }
            return;
        }

        // Statistik-Menü
        if (title.contains("Abgebaute Erze") && display.equals("← Zurück")) {
            openErzMenu(p);
            p.playSound(p.getLocation(), Sound.UI_BUTTON_CLICK, 1f, 1.0f);
        }
    }
}
