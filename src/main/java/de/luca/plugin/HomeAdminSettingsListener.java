package de.luca.plugin;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;

import java.util.UUID;

public class HomeAdminSettingsListener implements Listener {

    private final LucaCrafterPlugin plugin;

    public HomeAdminSettingsListener(LucaCrafterPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onClick(InventoryClickEvent e) {
        HumanEntity clicker = e.getWhoClicked();
        if (!(clicker instanceof Player)) return;

        String title = e.getView().getTitle();

        // Hauptmenü
        if (title.equals("§cHome-Admin")) {
            e.setCancelled(true);
            if (e.getCurrentItem() == null || e.getCurrentItem().getItemMeta() == null) return;

            String name = ChatColor.stripColor(e.getCurrentItem().getItemMeta().getDisplayName());
            Player p = (Player) clicker;

            if (name.equalsIgnoreCase("Spieler-Einstellungen")) {
                p.openInventory(new HomePlayerSelectGUI().getInventory());
            }
            return;
        }

        // Spieler-Auswahl
        if (title.equals("§cHomes: Spieler wählen")) {
            e.setCancelled(true);
            if (e.getCurrentItem() == null || e.getCurrentItem().getItemMeta() == null) return;

            String nameColored = e.getCurrentItem().getItemMeta().getDisplayName();
            String name = ChatColor.stripColor(nameColored);

            OfflinePlayer target = Bukkit.getOfflinePlayer(name);
            ((Player) clicker).openInventory(new HomePlayerSettingsGUI(plugin, target).getInventory());
            return;
        }

        // Spieler-Settings
        if (title.startsWith("§cHome-Settings: ")) {
            e.setCancelled(true);
            if (e.getCurrentItem() == null || e.getCurrentItem().getItemMeta() == null) return;

            String raw = ChatColor.stripColor(title); // "Home-Settings: Name"
            String playerName = raw.replace("Home-Settings: ", "").trim();
            OfflinePlayer target = Bukkit.getOfflinePlayer(playerName);
            UUID uuid = target.getUniqueId();

            ConfigManager cfg = plugin.getConfigManager();
            int slot = e.getRawSlot();
            boolean left = e.isLeftClick();
            boolean right = e.isRightClick();

            switch (slot) {
                case 10: { // Max Homes
                    int value = cfg.getMaxHomes(uuid);
                    if (left) value++;
                    if (right) value--;
                    if (value < 1) value = 1;
                    if (value > 100) value = 100;
                    cfg.setMaxHomes(uuid, value);
                    ((Player) clicker).openInventory(new HomePlayerSettingsGUI(plugin, target).getInventory());
                    break;
                }
                case 12: { // Cooldown
                    int value = cfg.getHomeCooldown(uuid);
                    if (left) value++;
                    if (right) value--;
                    if (value < 0) value = 0;
                    if (value > 3600) value = 3600;
                    cfg.setHomeCooldown(uuid, value);
                    ((Player) clicker).openInventory(new HomePlayerSettingsGUI(plugin, target).getInventory());
                    break;
                }
                case 14: { // Delay
                    int value = cfg.getHomeTeleportDelay(uuid);
                    if (left) value++;
                    if (right) value--;
                    if (value < 0) value = 0;
                    if (value > 60) value = 60;
                    cfg.setHomeTeleportDelay(uuid, value);
                    ((Player) clicker).openInventory(new HomePlayerSettingsGUI(plugin, target).getInventory());
                    break;
                }
                case 16: { // Instant-TP
                    boolean instant = cfg.isHomeInstantTeleport(uuid);
                    cfg.setHomeInstantTeleport(uuid, !instant);
                    ((Player) clicker).openInventory(new HomePlayerSettingsGUI(plugin, target).getInventory());
                    break;
                }
                case 19: { // Move-Cancel
                    boolean move = cfg.isHomeMoveCancelEnabled(uuid);
                    cfg.setHomeMoveCancelEnabled(uuid, !move);
                    ((Player) clicker).openInventory(new HomePlayerSettingsGUI(plugin, target).getInventory());
                    break;
                }
                case 21: { // Partikel
                    boolean particles = cfg.isHomeParticlesEnabled(uuid);
                    cfg.setHomeParticlesEnabled(uuid, !particles);
                    ((Player) clicker).openInventory(new HomePlayerSettingsGUI(plugin, target).getInventory());
                    break;
                }
                default:
                    break;
            }
        }
    }
}
