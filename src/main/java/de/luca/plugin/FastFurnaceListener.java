package de.luca.plugin;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Furnace;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.FurnaceSmeltEvent;
import org.bukkit.scheduler.BukkitRunnable;

/**
 * Dieses System sorgt dafür, dass Öfen in der Nähe von Spielern
 * schneller schmelzen. (Standardmäßig aktiv)
 */
public class FastFurnaceListener implements Listener {

    private final LucaCrafterPlugin plugin;

    public FastFurnaceListener(LucaCrafterPlugin plugin) {
        this.plugin = plugin;

        // Startet einen wiederkehrenden Task, der regelmäßig alle Öfen checkt
        new BukkitRunnable() {
            @Override
            public void run() {
                tickFurnaces();
            }
        }.runTaskTimer(plugin, 20L, 20L); // alle 1 Sekunde
    }

    private void tickFurnaces() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            Block block = player.getLocation().getBlock();

            int radius = 5;
            for (int x = -radius; x <= radius; x++) {
                for (int y = -radius; y <= radius; y++) {
                    for (int z = -radius; z <= radius; z++) {
                        Block nearby = block.getRelative(x, y, z);
                        if (nearby.getType() == Material.FURNACE ||
                            nearby.getType() == Material.BLAST_FURNACE ||
                            nearby.getType() == Material.SMOKER) {

                            Furnace f = (Furnace) nearby.getState();
                            if (f.getCookTime() > 0 && f.getCookTime() < 200) {
                                short newTime = (short) Math.min(200, f.getCookTime() + 5); // doppelte Geschwindigkeit
                                f.setCookTime(newTime);
                                f.update();
                            }
                        }
                    }
                }
            }
        }
    }

    @EventHandler
    public void onFurnaceSmelt(FurnaceSmeltEvent e) {
        // Nur ein Sound-Feedback für Spaß
        Block b = e.getBlock();
        b.getWorld().playSound(b.getLocation(), "block.furnace.fire_crackle", 0.3f, 1.2f);
    }
}
