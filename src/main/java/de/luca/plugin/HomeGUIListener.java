package de.luca.plugin;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;

public class HomeGUIListener implements Listener {

    private final LucaCrafterPlugin plugin;
    private final HomeTeleportHandler teleportHandler;

    public HomeGUIListener(LucaCrafterPlugin plugin) {
        this.plugin = plugin;

        // TeleportHandler direkt erstellen (einziger Listener)
        this.teleportHandler = new HomeTeleportHandler(plugin);
        plugin.getServer().getPluginManager().registerEvents(teleportHandler, plugin);
    }

    @EventHandler
    public void onClick(InventoryClickEvent e) {

        if (e.getView().getTitle().equals("§aDeine Homes")) {
            e.setCancelled(true);

            if (e.getCurrentItem() == null) return;

            Player p = (Player) e.getWhoClicked();

            String name = e.getCurrentItem().getItemMeta().getDisplayName()
                    .replace("§b", "");

            Home h = plugin.getHomeManager()
                    .getHomes(p.getUniqueId())
                    .get(name.toLowerCase());

            if (h != null) {
                p.closeInventory();

                // teleportHandler ist jetzt sicher
                HomeTeleportLogic.teleportPlayer(plugin, teleportHandler, p, h);
            }
        }
    }
}
