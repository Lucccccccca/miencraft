package de.luca.plugin;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;

import java.util.HashSet;
import java.util.Set;

public class BaumListener implements Listener {

    private final LucaCrafterPlugin plugin;
    private final BaumCommand baumCommand;
    private final int MAX_BLOCKS = 150;

    public BaumListener(LucaCrafterPlugin plugin, BaumCommand baumCommand) {
        this.plugin = plugin;
        this.baumCommand = baumCommand;
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent e) {
        Player p = e.getPlayer();

        // ✅ Aktiviert?
        if (!plugin.getConfigManager().getBaumEnabled(p.getUniqueId())) return;

        // ✅ Sneak-Pflicht prüfen
        if (plugin.getConfigManager().getBaumRequireSneak(p.getUniqueId()) && !p.isSneaking()) return;

        // ✅ Werkzeug prüfen – nur Äxte (auch Kupfer)
        ItemStack tool = p.getInventory().getItemInMainHand();
        if (tool == null || !isAxe(tool.getType())) return;

        Block block = e.getBlock();
        Material type = block.getType();

        // ✅ Nur Baumstämme
        if (!type.name().endsWith("_LOG")) return;

        Set<Block> visited = new HashSet<>();
        breakTree(block, type, visited);

        double factor = plugin.getConfigManager().getBaumDurabilityFactor(p.getUniqueId());
        boolean autoReplant = plugin.getConfigManager().getBaumAutoReplant(p.getUniqueId());
        int count = visited.size();

        // ✅ Bäume abbauen
        visited.forEach(b -> b.breakNaturally(tool));

        // ✅ Soundeffekt
        if (count > 4) {
            p.playSound(p.getLocation(), Sound.BLOCK_WOOD_BREAK, 1f, 1f);
        }

        // ✅ Haltbarkeit
        damageTool(tool, count, factor, p);

        // ✅ Auto-Replant
        if (autoReplant) {
            Bukkit.getScheduler().runTaskLater(plugin, () -> replantTree(block, type), 10L);
        }

        // ✅ Statistik
        plugin.getConfigManager().addTreeType(p.getUniqueId(), type, count);
    }

    // ============================
    // Hilfsfunktionen
    // ============================

    private boolean isAxe(Material mat) {
        return switch (mat) {
            case WOODEN_AXE, STONE_AXE, IRON_AXE, GOLDEN_AXE, DIAMOND_AXE, NETHERITE_AXE, COPPER_AXE -> true;
            default -> false;
        };
    }

    private void damageTool(ItemStack tool, int count, double factor, Player p) {
        if (tool == null || tool.getType().getMaxDurability() <= 0) return;
        short newDamage = (short) (tool.getDurability() + (count * factor));
        if (newDamage >= tool.getType().getMaxDurability()) {
            p.getInventory().setItemInMainHand(new ItemStack(Material.AIR));
            p.playSound(p.getLocation(), Sound.ENTITY_ITEM_BREAK, 1f, 1f);
        } else {
            tool.setDurability(newDamage);
        }
    }

    private void breakTree(Block start, Material type, Set<Block> visited) {
        if (visited.size() >= MAX_BLOCKS) return;
        if (visited.contains(start)) return;
        if (start.getType() != type) return;

        visited.add(start);

        for (int x = -1; x <= 1; x++) {
            for (int y = -1; y <= 1; y++) {
                for (int z = -1; z <= 1; z++) {
                    if (x == 0 && y == 0 && z == 0) continue;
                    Block relative = start.getRelative(x, y, z);
                    if (!visited.contains(relative) && relative.getType() == type) {
                        breakTree(relative, type, visited);
                    }
                }
            }
        }
    }

    private void replantTree(Block base, Material logType) {
        Block below = base.getRelative(BlockFace.DOWN);
        Material belowType = below.getType();
        if (belowType != Material.DIRT && belowType != Material.GRASS_BLOCK) return;

        Material sapling = switch (logType) {
            case OAK_LOG -> Material.OAK_SAPLING;
            case BIRCH_LOG -> Material.BIRCH_SAPLING;
            case SPRUCE_LOG -> Material.SPRUCE_SAPLING;
            case JUNGLE_LOG -> Material.JUNGLE_SAPLING;
            case ACACIA_LOG -> Material.ACACIA_SAPLING;
            case DARK_OAK_LOG -> Material.DARK_OAK_SAPLING;
            default -> null;
        };

        if (sapling != null) {
            base.getWorld().getBlockAt(base.getLocation()).setType(sapling);
        }
    }
}
