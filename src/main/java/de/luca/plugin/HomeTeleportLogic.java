package de.luca.plugin;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class HomeTeleportLogic {

    private final LucaCrafterPlugin plugin;
    private final ConfigManager config;

    public HomeTeleportLogic(LucaCrafterPlugin plugin) {
        this.plugin = plugin;
        this.config = plugin.getConfigManager();
    }

    public void teleport(Player player, Home home) {

        int delay = config.getHomeTeleportDelay(player.getUniqueId()); // pro Spieler
        boolean particles = config.isHomeParticlesEnabled(player.getUniqueId());

        if (delay <= 0) {
            player.teleport(home.getLocation());
            player.sendMessage("§aTeleportiert!");
            return;
        }

        player.sendMessage("§eTeleportiere in " + delay + " Sekunden…");

        new BukkitRunnable() {
            int time = delay;

            @Override
            public void run() {
                if (!player.isOnline()) {
                    cancel();
                    return;
                }

                if (time <= 0) {
                    player.teleport(home.getLocation());
                    player.sendMessage("§aTeleportiert!");
                    cancel();
                    return;
                }

                player.sendMessage("§7Teleport in §e" + time + "§7s…");
                time--;
            }

        }.runTaskTimer(plugin, 20, 20);
    }
}
