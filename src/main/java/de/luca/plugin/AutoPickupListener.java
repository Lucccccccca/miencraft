package de.luca.plugin;

import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockDropItemEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;

public class AutoPickupListener implements Listener {

    private final LucaCrafterPlugin plugin;

    public AutoPickupListener(LucaCrafterPlugin plugin) {
        this.plugin = plugin;
    }

    // 🪓 Block Drops automatisch ins Inventar
    @EventHandler
    public void onBlockDrop(BlockDropItemEvent e) {
        Player p = e.getPlayer();
        if (p == null) return;

        if (!plugin.getConfigManager().isAutoPickupEnabled(p.getUniqueId())) return;

        for (Item item : e.getItems()) {
            ItemStack stack = item.getItemStack();
            HashMap<Integer, ItemStack> leftover = p.getInventory().addItem(stack);
            if (leftover.isEmpty()) item.remove(); // ✅ Nur entfernen, wenn alles eingesammelt wurde
        }
    }

    // 🐷 Mob/Tier Drops automatisch ins Inventar
    @EventHandler
    public void onEntityDeath(EntityDeathEvent e) {
        if (e.getEntity().getKiller() == null) return;

        Player p = e.getEntity().getKiller();
        if (!plugin.getConfigManager().isAutoPickupEnabled(p.getUniqueId())) return;

        // Drops ins Inventar
        for (ItemStack drop : e.getDrops()) {
            HashMap<Integer, ItemStack> leftover = p.getInventory().addItem(drop);
            if (leftover.isEmpty()) drop.setAmount(0); // ✅ Nur leeren, wenn alles aufgenommen wurde
        }

        // ✅ Nur EINMAL löschen, nicht doppelt!
        e.getDrops().removeIf(d -> d.getAmount() == 0);
    }
}
