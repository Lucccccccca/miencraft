package de.luca.plugin;

import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class HomeTeleportLogic {

    private final LucaCrafterPlugin plugin;
    private final ConfigManager config;
    private final Map<UUID, Long> lastTeleport = new HashMap<>();

    public HomeTeleportLogic(LucaCrafterPlugin plugin) {
        this.plugin = plugin;
        this.config = plugin.getConfigManager();
    }

    public void teleport(Player player, Home home, HomeTeleportHandler handler) {

        UUID uuid = player.getUniqueId();

        int cooldown = config.getHomeCooldown(uuid);      // pro Spieler
        int delay = config.getHomeTeleportDelay(uuid);    // pro Spieler
        boolean instant = config.isHomeInstantTeleport(uuid);
        boolean moveCancel = config.isHomeMoveCancelEnabled(uuid);

        long now = System.currentTimeMillis();

        // Cooldown-Check
        if (cooldown > 0) {
            Long last = lastTeleport.get(uuid);
            if (last != null && (now - last) < cooldown * 1000L) {
                long remaining = (cooldown * 1000L - (now - last)) / 1000L;
                player.sendMessage("§cDu kannst §e/home §cin §e" + remaining + "§c Sekunden wieder benutzen.");
                return;
            }
        }

        // Instant-TP ignoriert Delay
        if (instant) {
            delay = 0;
        }

        // Sofort teleportieren
        if (delay <= 0) {
            player.teleport(home.getLocation());
            player.sendMessage("§aTeleportiert!");
            lastTeleport.put(uuid, now);
            if (handler != null) {
                handler.setMoveCancel(uuid, false);
            }
            return;
        }

        // Verzögerter Teleport
        player.sendMessage("§eTeleportiere in " + delay + " Sekunden…");

        final int startDelay = delay;
        final boolean useMoveCancel = moveCancel && handler != null;

        if (useMoveCancel) {
            handler.setMoveCancel(uuid, true);
        }

        new BukkitRunnable() {
            int time = startDelay;

            @Override
            public void run() {

                if (!player.isOnline()) {
                    if (useMoveCancel) {
                        handler.setMoveCancel(uuid, false);
                    }
                    cancel();
                    return;
                }

                // Abbruch durch Bewegung
                if (useMoveCancel && !handler.isMoveCancelled(uuid)) {
                    // Handler hat bereits die Nachricht gesendet
                    cancel();
                    return;
                }

                if (time <= 0) {
                    player.teleport(home.getLocation());
                    player.sendMessage("§aTeleportiert!");
                    lastTeleport.put(uuid, System.currentTimeMillis());
                    if (useMoveCancel) {
                        handler.setMoveCancel(uuid, false);
                    }
                    cancel();
                    return;
                }

                player.sendMessage("§7Teleport in §e" + time + "§7s…");
                time--;
            }

        }.runTaskTimer(plugin, 20, 20);
    }

    // Statischer Helper für Listener
    public static void teleportPlayer(LucaCrafterPlugin plugin,
                                      HomeTeleportHandler handler,
                                      Player player,
                                      Home home) {
        plugin.getHomeTeleportLogic().teleport(player, home, handler);
    }
}
