package de.luca.plugin;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.entity.EntityType;

public class AntiCreeperListener implements Listener {

    private final LucaCrafterPlugin plugin;

    public AntiCreeperListener(LucaCrafterPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onCreeperExplode(EntityExplodeEvent e) {
        if (e.getEntityType() != EntityType.CREEPER) return;

        // Config prüfen
        boolean enabled = plugin.getConfig().getBoolean("antigrief.creeper-disable-blocks", true);
        if (!enabled) return;

        // Block-Zerstörung verhindern
        e.blockList().clear();
        e.setYield(0);
    }
}
