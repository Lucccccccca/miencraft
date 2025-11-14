package de.luca.plugin;

import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class HomeMarkerTask extends BukkitRunnable {

    private final LucaCrafterPlugin plugin;
    private final HomeManager homeManager;

    public HomeMarkerTask(LucaCrafterPlugin plugin, HomeManager homeManager) {
        this.plugin = plugin;
        this.homeManager = homeManager;
    }

    @Override
    public void run() {

        for (Player p : Bukkit.getOnlinePlayers()) {

            boolean show = plugin.getConfigManager().isHomeParticlesEnabled(p.getUniqueId());
            if (!show) continue;

            homeManager.getHomes(p.getUniqueId()).values().forEach(home -> {
                Location l = home.getLocation().clone().add(0, 0.1, 0);

                for (double t = 0; t < Math.PI * 2; t += Math.PI / 12) {
                    double x = Math.cos(t) * 0.8;
                    double z = Math.sin(t) * 0.8;

                    p.spawnParticle(
                            Particle.DUST,
                            l.getX() + x,
                            l.getY(),
                            l.getZ() + z,
                            1,
                            new Particle.DustOptions(Color.fromRGB(255, 100, 0), 1.2f)
                    );
                }
            });
        }
    }
}
