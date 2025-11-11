package de.luca.plugin;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

public class SettingsGUIListener implements Listener {

    private final LucaCrafterPlugin plugin;

    public SettingsGUIListener(LucaCrafterPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onSettingsClick(InventoryClickEvent e) {
        if (!(e.getWhoClicked() instanceof Player p)) return;
        if (e.getClickedInventory() == null || e.getCurrentItem() == null) return;

        // Nur in "Spieler-Einstellungen" reagieren
        if (!e.getView().getTitle().equals(SettingsPlayerGUI.TITLE)) return;

        e.setCancelled(true); // verhindert, dass Items rausgenommen werden
        ItemStack item = e.getCurrentItem();
        if (!item.hasItemMeta() || !item.getItemMeta().hasDisplayName()) return;

        String name = ChatColor.stripColor(item.getItemMeta().getDisplayName());

        switch (name) {
            case "🧲 Magnet" -> {
                boolean newState = !plugin.getConfigManager().isMagnetEnabled(p.getUniqueId());
                plugin.getConfigManager().setMagnetEnabled(p.getUniqueId(), newState);
                p.sendMessage(ChatColor.YELLOW + "🧲 Magnet wurde " + (newState ? "§aaktiviert" : "§cdeaktiviert") + ChatColor.YELLOW + ".");
                new SettingsPlayerGUI(plugin).open(p);
            }

            case "📦 AutoPickup" -> {
                boolean newState = !plugin.getConfigManager().isAutoPickupEnabled(p.getUniqueId());
                plugin.getConfigManager().setAutoPickupEnabled(p.getUniqueId(), newState);
                p.sendMessage(ChatColor.GOLD + "📦 AutoPickup wurde " + (newState ? "§aaktiviert" : "§cdeaktiviert") + ChatColor.GOLD + ".");
                new SettingsPlayerGUI(plugin).open(p);
            }

            case "🔥 Schneller Ofen" -> {
                boolean newState = !plugin.getConfigManager().isFastFurnaceEnabled(p.getUniqueId());
                plugin.getConfigManager().setFastFurnaceEnabled(p.getUniqueId(), newState);
                p.sendMessage(ChatColor.RED + "🔥 Schneller Ofen wurde " + (newState ? "§aaktiviert" : "§cdeaktiviert") + ChatColor.RED + ".");
                new SettingsPlayerGUI(plugin).open(p);
            }

            case "⬅ Zurück" -> {
                p.closeInventory();
                p.sendMessage(ChatColor.GRAY + "Menü geschlossen.");
            }

            default -> p.sendMessage(ChatColor.RED + "Dieser Button hat keine Funktion!");
        }
    }
}
