package de.luca.plugin;

import org.bukkit.GameMode;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

public class SettingsListener implements Listener {

    private final LucaCrafterPlugin plugin;

    public SettingsListener(LucaCrafterPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent e) {
        if (!(e.getWhoClicked() instanceof Player p)) return;
        if (e.getView().getTitle() == null) return;
        if (e.getCurrentItem() == null) return;

        ItemStack item = e.getCurrentItem();
        String title = e.getView().getTitle();

        // ❗ Nur blockieren, wenn es eines deiner Custom-GUIs ist
        if (title.equals("§b⚙ Einstellungen") ||
            title.equals("§a🧍 Spieler-Einstellungen") ||
            title.equals("§e⚙ Server-Einstellungen")) {
            e.setCancelled(true);
        } else {
            return; // alle anderen Inventare (z. B. Ofen, Truhen) nicht beeinflussen
        }

        // === Hauptmenü ===
        switch (title) {
            case "§b⚙ Einstellungen" -> {
                switch (item.getType()) {
                    case PLAYER_HEAD -> new SettingsPlayerGUI(plugin).open(p);
                    case COMMAND_BLOCK -> {
                        if (p.isOp()) SettingsServerGUI.open(p, plugin);
                        else p.sendMessage("§cNur Admins können Server-Einstellungen öffnen!");
                    }
                    case BARRIER -> p.closeInventory();
                }
            }

            // === Spieler-Einstellungen ===
            case "§a🧍 Spieler-Einstellungen" -> {
                switch (item.getType()) {
                    case FEATHER -> {
                        boolean fly = !p.getAllowFlight();
                        p.setAllowFlight(fly);
                        p.sendMessage("§bFly " + (fly ? "aktiviert" : "deaktiviert"));
                    }
                    case GOLDEN_APPLE -> p.setHealth(p.getMaxHealth());
                    case COOKED_BEEF -> p.setFoodLevel(20);
                    case COMPASS -> p.sendMessage("§dMagnet ist aktuell standardmäßig aktiv!"); // 🧲 Magnet immer aktiv
                    case FURNACE -> p.sendMessage("§7Fast Furnace ist standardmäßig aktiv!");
                    case BARRIER -> SettingsMainGUI.open(p, plugin);
                }
                p.playSound(p.getLocation(), Sound.UI_BUTTON_CLICK, 1f, 1f);
            }

            // === Server-Einstellungen ===
            case "§e⚙ Server-Einstellungen" -> {
                if (!p.isOp()) {
                    p.sendMessage("§cNur Admins dürfen das!");
                    p.closeInventory();
                    return;
                }

                switch (item.getType()) {
                    case WHEAT -> {
                        boolean fp = !plugin.getConfigManager().isFarmProtectEnabled();
                        plugin.getConfigManager().setFarmProtectEnabled(fp);
                        p.sendMessage("§aFarm-Protection " + (fp ? "aktiviert" : "deaktiviert"));
                    }
                    case CREEPER_HEAD -> {
                        boolean ac = !plugin.getConfigManager().isAntiCreeperEnabled();
                        plugin.getConfigManager().setAntiCreeperEnabled(ac);
                        p.sendMessage("§cAnti-Creeper " + (ac ? "aktiviert" : "deaktiviert"));
                    }
                    case IRON_SWORD -> {
                        boolean pvp = !plugin.getConfigManager().isPvpEnabled();
                        plugin.getConfigManager().setPvpEnabled(pvp);
                        p.sendMessage("§fPvP " + (pvp ? "aktiviert" : "deaktiviert"));
                    }
                    case BARRIER -> SettingsMainGUI.open(p, plugin);
                }
                p.playSound(p.getLocation(), Sound.UI_BUTTON_CLICK, 1f, 1f);
            }
        }
    }
}
