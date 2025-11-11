package de.luca.plugin;

import org.bukkit.Bukkit;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.scheduler.BukkitTask;
import java.util.List;
import java.util.UUID;

public class MagnetListener implements Listener {

    private final LucaCrafterPlugin plugin;
    private BukkitTask magnetTask;

    public MagnetListener(LucaCrafterPlugin plugin) {
        this.plugin = plugin;

        // Task läuft alle 10 Ticks (~0.5 Sekunden)
        magnetTask = Bukkit.getScheduler().runTaskTimer(plugin, () -> {
            for (Player p : Bukkit.getOnlinePlayers()) {
                UUID id = p.getUniqueId();
                if (!plugin.getConfigManager().isMagnetEnabled(id)) continue;

                List<Item> items = p.getNearbyEntities(5, 5, 5).stream()
                        .filter(e -> e instanceof Item)
                        .map(e -> (Item) e)
                        .toList();

                for (Item item : items) {
                    if (item.isDead() || !item.isValid()) continue;
                    if (p.getInventory().firstEmpty() != -1) {
                        p.getInventory().addItem(item.getItemStack());
                        item.remove();
                    }
                }
            }
        }, 20L, 10L);
    }
}
