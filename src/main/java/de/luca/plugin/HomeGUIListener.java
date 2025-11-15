package de.luca.plugin;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;

public class HomeGUIListener implements Listener {

    private final LucaCrafterPlugin plugin;
    private final HomeTeleportHandler teleportHandler;

    public HomeGUIListener(LucaCrafterPlugin plugin, HomeTeleportHandler handler) {
        this.plugin = plugin;
        this.teleportHandler = handler;
    }

    @EventHandler
    public void onClick(InventoryClickEvent e) {

        if (!e.getView().getTitle().equals("§aDeine Homes")) return;

        e.setCancelled(true);

        if (e.getCurrentItem() == null || e.getCurrentItem().getItemMeta() == null)
            return;

        Player p = (Player) e.getWhoClicked();
        Material mat = e.getCurrentItem().getType();
        int slot = e.getRawSlot();

        // Partikel toggle
        if (slot == 48) {
            boolean now = plugin.getConfigManager().isHomeParticlesEnabled(p.getUniqueId());
            plugin.getConfigManager().setHomeParticlesEnabled(p.getUniqueId(), !now);
            p.sendMessage("§aHome-Partikel sind nun: " + (!now ? "§aAktiv" : "§cDeaktiviert"));
            p.openInventory(new HomeMainGUI(plugin, p).getInventory());
            return;
        }

        // Hologramm toggle
        if (slot == 50) {
            boolean now = plugin.getConfigManager().isHomeHologramEnabled(p.getUniqueId());
            plugin.getConfigManager().setHomeHologramEnabled(p.getUniqueId(), !now);

            if (now) {
                plugin.getHomeHologramManager().removeAll(p);
                p.sendMessage("§cHome-Hologramme deaktiviert.");
            } else {
                plugin.getHomeHologramManager().refreshPlayer(p);
                p.sendMessage("§aHome-Hologramme aktiviert.");
            }

            p.openInventory(new HomeMainGUI(plugin, p).getInventory());
            return;
        }

        // Sortier-Modus wechseln
        if (slot == 46) {
            HomeSortMode current = plugin.getConfigManager().getHomeSortMode(p.getUniqueId());
            HomeSortMode next;
            switch (current) {
                case NAME:
                    next = HomeSortMode.DISTANCE;
                    break;
                case DISTANCE:
                    next = HomeSortMode.WORLD;
                    break;
                case WORLD:
                default:
                    next = HomeSortMode.NAME;
                    break;
            }
            plugin.getConfigManager().setHomeSortMode(p.getUniqueId(), next);
            p.sendMessage("§aSortierung geändert zu: §e" + next.name());
            p.openInventory(new HomeMainGUI(plugin, p).getInventory());
            return;
        }

        // Home Aktionen
        if (mat == Material.ENDER_PEARL) {

            String name = ChatColor.stripColor(
                    e.getCurrentItem().getItemMeta().getDisplayName()
            );

            Home h = plugin.getHomeManager()
                    .getHomes(p.getUniqueId())
                    .get(name.toLowerCase());

            if (h == null) return;

            // Shift + Rechtsklick → Privacy ändern
            if (e.isRightClick() && e.isShiftClick()) {
                HomePrivacy current = plugin.getConfigManager().getHomePrivacy(p.getUniqueId(), name);
                HomePrivacy next = current.next();
                plugin.getConfigManager().setHomePrivacy(p.getUniqueId(), name, next);
                p.sendMessage("§aPrivatsphäre für §e" + name + " §aist nun: " + next.getDisplayName());
                p.openInventory(new HomeMainGUI(plugin, p).getInventory());
                return;
            }

            // Rechtsklick → Home löschen
            if (e.isRightClick()) {
                plugin.getHomeManager().deleteHome(p.getUniqueId(), name.toLowerCase());
                plugin.getHomeHologramManager().removeHologram(p, name);
                p.sendMessage("§cHome §e" + name + " §cwurde gelöscht.");
                p.openInventory(new HomeMainGUI(plugin, p).getInventory());
                return;
            }

            // Linksklick → Teleport
            if (e.isLeftClick()) {
                p.closeInventory();
                HomeTeleportLogic.teleportPlayer(plugin, teleportHandler, p, h);
            }
        }
    }
}
