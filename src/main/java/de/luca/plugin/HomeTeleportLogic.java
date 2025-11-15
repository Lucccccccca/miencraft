package de.luca.plugin;

import org.bukkit.Particle;
import org.bukkit.Sound;
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

        int cooldown = config.getHomeCooldown(uuid);
        int delay = config.getHomeTeleportDelay(uuid);
        boolean instant = config.isHomeInstantTeleport(uuid);
        boolean moveCancel = config.isHomeMoveCancelEnabled(uuid);

        long now = System.currentTimeMillis();

        // Cooldown
        if (cooldown > 0) {
            Long last = lastTeleport.get(uuid);
            if (last != null && (now - last) < cooldown * 1000L) {
                long remaining = (cooldown * 1000L - (now - last)) / 1000L;
                player.sendMessage("§cDu kannst §e/home §cin §e" + remaining + "§c Sekunden wieder benutzen.");
                return;
            }
        }

        if (instant) {
            delay = 0;
        }

        // Sofortiger Teleport
        if (delay <= 0) {
            playStartEffects(player);
            player.teleport(home.getLocation());
            playEndEffects(player);
            player.sendMessage("§aTeleportiert!");
            lastTeleport.put(uuid, System.currentTimeMillis());
            if (handler != null) {
                handler.setMoveCancel(uuid, false);
            }
            return;
        }

        // Verzögerter Teleport mit Animation
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

                // Move-Cancel
                if (useMoveCancel && !handler.isMoveCancelled(uuid)) {
                    // Handler hat bereits Nachricht gesendet
                    cancel();
                    return;
                }

                if (time <= 0) {
                    playStartEffects(player);
                    player.teleport(home.getLocation());
                    playEndEffects(player);
                    player.sendMessage("§aTeleportiert!");
                    lastTeleport.put(uuid, System.currentTimeMillis());
                    if (useMoveCancel) {
                        handler.setMoveCancel(uuid, false);
                    }
                    cancel();
                    return;
                }

                // Countdown-Message + Animation
                player.sendMessage("§7Teleport in §e" + time + "§7s…");
                spawnCountdownParticles(player);
                time--;
            }

        }.runTaskTimer(plugin, 20, 20);
    }

    private void spawnCountdownParticles(Player player) {
        player.getWorld().spawnParticle(
                Particle.END_ROD,
                player.getLocation().add(0, 1.0, 0),
                10,
                0.3,
                0.4,
                0.3,
                0.01
        );
        player.getWorld().playSound(player.getLocation(), Sound.BLOCK_AMETHYST_BLOCK_CHIME, 0.3f, 1.8f);
    }

    private void playStartEffects(Player player) {
        player.getWorld().spawnParticle(
                Particle.END_ROD,
                player.getLocation().add(0, 1.0, 0),
                40,
                0.5,
                0.7,
                0.5,
                0.02
        );
        player.getWorld().playSound(player.getLocation(), Sound.BLOCK_BEACON_POWER_SELECT, 0.6f, 1.5f);
    }

    private void playEndEffects(Player player) {
        player.getWorld().spawnParticle(
                Particle.GLOW,
                player.getLocation().add(0, 1.0, 0),
                40,
                0.7,
                0.7,
                0.7,
                0.01
        );
        player.getWorld().playSound(player.getLocation(), Sound.BLOCK_BEACON_ACTIVATE, 0.8f, 1.3f);
    }

    public static void teleportPlayer(LucaCrafterPlugin plugin,
                                      HomeTeleportHandler handler,
                                      Player player,
                                      Home home) {
        plugin.getHomeTeleportLogic().teleport(player, home, handler);
    }
}
