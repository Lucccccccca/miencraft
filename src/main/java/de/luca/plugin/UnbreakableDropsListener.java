package de.luca.plugin;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;

import java.util.EnumSet;
import java.util.Set;

public class UnbreakableDropsListener implements Listener {

    private static final Set<Material> ALWAYS_DROP = EnumSet.of(
            Material.GLASS,
            Material.GLASS_PANE,
            Material.WHITE_STAINED_GLASS,
            Material.BLACK_STAINED_GLASS,
            Material.LIGHT_BLUE_STAINED_GLASS,
            Material.ENDER_CHEST,
            Material.SPAWNER,
            Material.BEACON
    );

    @EventHandler
    public void onBlockBreak(BlockBreakEvent e) {
        Block b = e.getBlock();
        if (!ALWAYS_DROP.contains(b.getType())) return;

        e.setDropItems(false); // Verhindert Vanilla-Drops
        b.getWorld().dropItemNaturally(b.getLocation(), new ItemStack(b.getType()));
    }
}
