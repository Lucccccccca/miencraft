package de.luca.plugin;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.UUID;

public class AfkPlugin {

    private final LucaCrafterPlugin plugin;
    private final HashMap<UUID, Long> lastMovement = new HashMap<>();

    public AfkPlugin(LucaCrafterPlugin plugin) {
        this.plugin = plugin;
        // überprüft alle 10 Sekunden
        Bukkit.getScheduler().runTaskTimer(plugin, this::checkAfkPlayers, 0L, 200L);
    }

    public void updateActivity(Player player) {
        lastMovement.put(player.getUniqueId(), System.currentTimeMillis());
    }

    public boolean isAfk(Player player) {
        return player.getGameMode() == GameMode.SPECTATOR;
    }

    public void checkAfkPlayers() {
        long now = System.currentTimeMillis();
        for (Player player : Bukkit.getOnlinePlayers()) {
            long last = lastMovement.getOrDefault(player.getUniqueId(), now);
            if (!isAfk(player) && now - last > 3 * 60 * 1000) { // 3 Minuten
                player.setGameMode(GameMode.SPECTATOR);
                Bukkit.broadcastMessage("💤 " + player.getName() + " ist jetzt AFK.");
            }
        }
    }
}
