package de.luca.plugin;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.Tag;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayDeque;
import java.util.HashSet;
import java.util.Set;

public class ErzListener implements Listener {

    private final LucaCrafterPlugin plugin;
    private final ErzCommand erzCommand;
    private static final int MAX_BLOCKS = 250;

    public ErzListener(LucaCrafterPlugin plugin, ErzCommand erzCommand) {
        this.plugin = plugin;
        this.erzCommand = erzCommand;
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent e) {
        Player player = e.getPlayer();
        Block start = e.getBlock();
        Material startType = start.getType();

        // Aktiv?
        if (!plugin.getConfigManager().getErzEnabled(player.getUniqueId())) return;

        // Sneaken nötig?
        boolean requireSneak = plugin.getConfigManager().getErzRequireSneak(player.getUniqueId());
        if (requireSneak && !player.isSneaking()) return;

        // Nur Erze?
        boolean onlyStandard = plugin.getConfigManager().getErzOnlyStandard(player.getUniqueId());
        if (!isOre(startType, onlyStandard)) return;

        // Pickaxe?
        ItemStack tool = player.getInventory().getItemInMainHand();
        if (!isPickaxe(tool.getType())) {
            player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_BASS, 1f, 0.8f);
            player.sendMessage("§c❌ Du brauchst eine Spitzhacke, um Erze abzubauen!");
            e.setCancelled(true);
            return;
        }

        // Referenztyp normalisieren (DEEPSLATE_*_ORE -> *_ORE)
        Material baseOre = normalizeOreType(startType);

        // Cluster über 6 Nachbarn (kein 3x3x3 Flood)
        Set<Block> cluster = findCluster6Dir(start, baseOre);

        if (cluster.isEmpty()) return;

        // Blöcke abbauen
        for (Block b : cluster) b.breakNaturally(tool);

        // Sound
        player.playSound(
                player.getLocation(),
                cluster.size() > 3 ? Sound.BLOCK_AMETHYST_BLOCK_BREAK : Sound.BLOCK_STONE_BREAK,
                1f, cluster.size() > 3 ? 1.2f : 1f
        );

        // Haltbarkeit
        if (tool.getType().getMaxDurability() > 0 && tool.getItemMeta() != null) {
            double factor = plugin.getConfigManager().getErzDurabilityFactor(player.getUniqueId());
            short newDamage = (short) (tool.getDurability() + (cluster.size() * factor));
            if (newDamage < tool.getType().getMaxDurability()) {
                tool.setDurability(newDamage);
            } else {
                player.getInventory().setItemInMainHand(new ItemStack(Material.AIR));
                player.playSound(player.getLocation(), Sound.ENTITY_ITEM_BREAK, 1f, 1f);
            }
        }

        // Stats
        plugin.getConfigManager().addOreType(player.getUniqueId(), baseOre, cluster.size());
        Bukkit.getLogger().info(player.getName() + " hat " + cluster.size() + "x " + baseOre.name() + " abgebaut.");
    }

    private boolean isPickaxe(Material m) {
        return switch (m) {
            case WOODEN_PICKAXE, STONE_PICKAXE, IRON_PICKAXE, GOLDEN_PICKAXE, DIAMOND_PICKAXE, NETHERITE_PICKAXE -> true;
            default -> false;
        };
    }

/** Nur echte Erze zulassen (fix für das Deepslate-„Alles ist Erz“-Problem). */
private boolean isOre(Material mat, boolean onlyStandard) {
    // 1) Bukkit-Tag prüfen (wenn vorhanden, ab 1.17+)
    try {
        Class<?> tagClass = Class.forName("org.bukkit.Tag");
        Object oresTag = tagClass.getField("ORES").get(null);
        if (oresTag != null && (boolean) oresTag.getClass().getMethod("isTagged", Material.class).invoke(oresTag, mat)) {
            return true;
        }
    } catch (Exception ignored) {
        // Kein Tag vorhanden – ältere API, überspringen
    }

    // 2) Fallback-Whitelist (funktioniert auf allen Servern)
    Set<Material> standard = Set.of(
            Material.COAL_ORE, Material.IRON_ORE, Material.GOLD_ORE,
            Material.REDSTONE_ORE, Material.LAPIS_ORE, Material.DIAMOND_ORE,
            Material.EMERALD_ORE, Material.COPPER_ORE,
            Material.DEEPSLATE_COAL_ORE, Material.DEEPSLATE_IRON_ORE,
            Material.DEEPSLATE_GOLD_ORE, Material.DEEPSLATE_REDSTONE_ORE,
            Material.DEEPSLATE_LAPIS_ORE, Material.DEEPSLATE_DIAMOND_ORE,
            Material.DEEPSLATE_EMERALD_ORE, Material.DEEPSLATE_COPPER_ORE,
            Material.NETHER_GOLD_ORE, Material.NETHER_QUARTZ_ORE
    );

    if (onlyStandard) return standard.contains(mat);

    // „Erweitert“ = alles was *_ORE ist – aber NICHT anhand „contains(DEEPSLATE)“!
    return mat.name().endsWith("_ORE");
}


    /** DEEPSLATE_*_ORE -> *_ORE (für Cluster-Gleichheit). */
    private Material normalizeOreType(Material mat) {
        String n = mat.name();
        if (n.startsWith("DEEPSLATE_") && n.endsWith("_ORE")) {
            String base = n.replace("DEEPSLATE_", "");
            try {
                return Material.valueOf(base);
            } catch (IllegalArgumentException ignored) {}
        }
        return mat;
    }

    /** BFS über 6 Nachbarn, begrenzt durch MAX_BLOCKS. */
    private Set<Block> findCluster6Dir(Block start, Material baseOre) {
        Set<Block> out = new HashSet<>();
        ArrayDeque<Block> q = new ArrayDeque<>();
        q.add(start);

        final int[][] DIRS = {
                { 1, 0, 0}, {-1, 0, 0},
                { 0, 1, 0}, { 0,-1, 0},
                { 0, 0, 1}, { 0, 0,-1}
        };

        while (!q.isEmpty() && out.size() < MAX_BLOCKS) {
            Block b = q.poll();
            if (out.contains(b)) continue;

            // nur gleiche (normalisierte) Erzart
            if (normalizeOreType(b.getType()) != baseOre) continue;

            out.add(b);

            for (int[] d : DIRS) {
                Block n = b.getRelative(d[0], d[1], d[2]);
                if (!out.contains(n) && normalizeOreType(n.getType()) == baseOre) {
                    q.add(n);
                }
            }
        }
        return out;
    }
}
