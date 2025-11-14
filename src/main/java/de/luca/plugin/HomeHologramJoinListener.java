package de.luca.plugin;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class HomeHologramJoinListener implements Listener {

    private final LucaCrafterPlugin plugin;

    public HomeHologramJoinListener(LucaCrafterPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        plugin.getHomeHologramManager().refreshPlayer(e.getPlayer());
    }
}
