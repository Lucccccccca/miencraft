package de.luca.plugin;

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
        // particles wird im Moment nur für MarkerTask genutzt – hier ignoriert

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

    // 🔧 Statischer Helper für deinen Listener (schnelle Variante A)
    public static void teleportPlayer(LucaCrafterPlugin plugin,
                                      HomeTeleportHandler handler,
                                      Player player,
                                      Home home) {
        // aktuell ignorieren wir handler (Move-Cancel-System),
        // und benutzen nur die bestehende Delay-Logik:
        new HomeTeleportLogic(plugin).teleport(player, home);
    }
}
