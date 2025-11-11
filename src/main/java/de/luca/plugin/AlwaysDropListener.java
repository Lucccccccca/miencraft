package de.luca.plugin;

import org.bukkit.event.Listener;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

public class AlwaysDropListener implements Listener {

    private final LucaCrafterPlugin plugin;

    // ✅ Konstruktor hinzufügen!
    public AlwaysDropListener(LucaCrafterPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent e) {
        Block block = e.getBlock();
        Player p = e.getPlayer();

        // Diese Blöcke sollen IMMER droppen
        Material type = block.getType();
        if (type == Material.GLASS ||
            type == Material.GLASS_PANE ||
            type == Material.END_PORTAL_FRAME ||
            type == Material.ENDER_CHEST ||
            type == Material.BEACON ||
            type == Material.SEA_LANTERN ||
            type == Material.LANTERN ||
            type == Material.SHROOMLIGHT) {

            block.getWorld().dropItemNaturally(block.getLocation(), new org.bukkit.inventory.ItemStack(type));
        }
    }
}
