package de.luca.plugin;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class AfkListener implements Listener {

    private final AfkPlugin afkSystem;

    public AfkListener(AfkPlugin afkSystem) {
        this.afkSystem = afkSystem;
    }

    @EventHandler
    public void onMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        afkSystem.updateActivity(player);

        if (player.getGameMode() == GameMode.SPECTATOR) {
            player.setGameMode(GameMode.SURVIVAL);
            Bukkit.broadcastMessage("😎 " + player.getName() + " ist wieder aktiv!");
        }
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        afkSystem.updateActivity(event.getPlayer());
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        afkSystem.updateActivity(event.getPlayer());
    }
}
