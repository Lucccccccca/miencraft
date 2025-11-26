package de.luca.plugin;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class PrefixJoinListener implements Listener {

    private final LucaCrafterPlugin plugin;

    public PrefixJoinListener(LucaCrafterPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        // lädt Prefix beim Join
        plugin.getPrefixUpdater().update(e.getPlayer());
    }
}
