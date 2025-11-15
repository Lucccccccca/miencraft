package de.luca.plugin;

import de.luca.plugin.Home;
import de.luca.plugin.HomeTeleportHandler;
import de.luca.plugin.HomeTeleportLogic;
import de.luca.plugin.LucaCrafterPlugin;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;

public class FriendHomesGUIListener implements Listener {

    private final LucaCrafterPlugin plugin;
    private final HomeTeleportHandler handler;

    public FriendHomesGUIListener(LucaCrafterPlugin plugin, HomeTeleportHandler handler) {
        this.plugin = plugin;
        this.handler = handler;
    }

    @EventHandler
    public void onClick(InventoryClickEvent e) {

        if (!e.getView().getTitle().startsWith("§aHomes von ")) return;

        e.setCancelled(true);

        if (e.getCurrentItem() == null || e.getCurrentItem().getItemMeta() == null)
            return;

        Player viewer = (Player) e.getWhoClicked();
        Material mat = e.getCurrentItem().getType();

        if (mat != Material.ENDER_PEARL) return;

        String homeName = ChatColor.stripColor(e.getCurrentItem().getItemMeta().getDisplayName());
        String friendName = ChatColor.stripColor(e.getView().getTitle().replace("Homes von ", "").replace("§a", "").replace("§e", ""));

        // Finde UUID des Freundes
        plugin.getServer().getOnlinePlayers().stream()
                .filter(pl -> pl.getName().equals(friendName))
                .findFirst()
                .ifPresent(friend -> {

                    Home h = plugin.getHomeManager().getHomes(friend.getUniqueId()).get(homeName.toLowerCase());
                    if (h == null) {
                        viewer.sendMessage("§cDieses Home existiert nicht mehr.");
                        return;
                    }

                    viewer.closeInventory();
                    HomeTeleportLogic.teleportPlayer(plugin, handler, viewer, h);
                });
    }
}
