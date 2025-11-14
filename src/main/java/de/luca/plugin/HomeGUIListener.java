package de.luca.plugin;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;

public class HomeGUIListener implements Listener {

    private final LucaCrafterPlugin plugin;

    public HomeGUIListener(LucaCrafterPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onClick(InventoryClickEvent e) {

        if (e.getView().getTitle().equals("§aDeine Homes")) {
            e.setCancelled(true);

            if (e.getCurrentItem() == null) return;

            Player p = (Player) e.getWhoClicked();

            String name = e.getCurrentItem().getItemMeta().getDisplayName()
                    .replace("§b", "");

            Home h = plugin.getHomeManager().getHomes(p.getUniqueId()).get(name.toLowerCase());

            if (h != null) {
                p.closeInventory();

                HomeTeleportHandler tp = (HomeTeleportHandler)
                        plugin.getServer().getPluginManager().getRegisteredListeners(plugin)
                        .stream().filter(l -> l.getListener() instanceof HomeTeleportHandler)
                        .findFirst().get().getListener();

                HomeTeleportLogic.teleportPlayer(plugin, tp, p, h);
            }
        }
    }
}
