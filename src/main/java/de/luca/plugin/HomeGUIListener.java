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

        // ================= PARTIKEL TOGGLE ==================
        if (e.getRawSlot() == 49) {
            boolean now = plugin.getConfigManager().isHomeParticlesEnabled(p.getUniqueId());
            plugin.getConfigManager().setHomeParticlesEnabled(p.getUniqueId(), !now);
            p.sendMessage("§aHome-Partikel sind nun: "
                    + (!now ? "§aAktiv" : "§cDeaktiviert"));
            p.openInventory(new HomeMainGUI(plugin, p).getInventory());
            return;
        }



        // ====== HOLOGRAMM toggle =======
if (e.getRawSlot() == 50) {
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



        // ================= HOME AKTIONEN ==================
        if (mat == Material.ENDER_PEARL) {

            String name = ChatColor.stripColor(
                    e.getCurrentItem().getItemMeta().getDisplayName()
            );

            Home h = plugin.getHomeManager()
                    .getHomes(p.getUniqueId())
                    .get(name.toLowerCase());

            if (h == null) return;

            // Rechtsklick → Home löschen
            if (e.isRightClick()) {
                plugin.getHomeManager().deleteHome(p.getUniqueId(), name.toLowerCase());
                p.sendMessage("§cHome §e" + name + " §cwurde gelöscht.");
                p.openInventory(new HomeMainGUI(plugin, p).getInventory());
                return;
            }

            // Linksklick → Teleport
            if (e.isLeftClick()) {
                p.closeInventory();
                HomeTeleportLogic.teleportPlayer(plugin, teleportHandler, p, h);
                return;
            }
        }
    }
}
