package de.luca.plugin;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.data.Ageable;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.EntityBlockFormEvent;
import org.bukkit.event.entity.EntityChangeBlockEvent;
import org.bukkit.event.player.PlayerInteractEvent;

/**
 * Verhindert das Zerstampfen von Ackerboden & schützt Crops,
 * wenn server.farm_protect = true
 */
public class FarmProtectListener implements Listener {

    private final LucaCrafterPlugin plugin;

    public FarmProtectListener(LucaCrafterPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onTrample(EntityChangeBlockEvent e) {
        if (!plugin.getConfigManager().isFarmProtectEnabled()) return;
        Block b = e.getBlock();
        if (b.getType() == Material.FARMLAND && e.getTo() == Material.DIRT) {
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onRightClick(PlayerInteractEvent e) {
        if (!plugin.getConfigManager().isFarmProtectEnabled()) return;
        if (e.getAction() != Action.PHYSICAL) return;
        Block b = e.getClickedBlock();
        if (b == null) return;
        if (b.getType() == Material.FARMLAND) {
            e.setCancelled(true);
        }
    }

    // Option: wenn du willst, dass voll gereifte Crops nicht zerstört werden:
    @EventHandler
    public void onForm(EntityBlockFormEvent e) {
        if (!plugin.getConfigManager().isFarmProtectEnabled()) return;
        Block b = e.getBlock();
        if (b.getBlockData() instanceof Ageable age && b.getType().toString().endsWith("_WHEAT")) {
            // Nur Beispiel – i.d.R. reicht das Trample-Blocken
        }
    }
}
