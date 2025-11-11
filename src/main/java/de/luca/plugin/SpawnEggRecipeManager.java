package de.luca.plugin;

import org.bukkit.*;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class SpawnEggRecipeManager {

    private final JavaPlugin plugin;
    private final Set<EntityType> blocked = EnumSet.of(EntityType.ENDER_DRAGON, EntityType.WARDEN);

    // Hauptzutaten pro Mob
    private static final Map<EntityType, Material> MOB_INGREDIENTS = new HashMap<>();

    static {
        MOB_INGREDIENTS.put(EntityType.ZOMBIE, Material.ROTTEN_FLESH);
        MOB_INGREDIENTS.put(EntityType.SKELETON, Material.BONE);
        MOB_INGREDIENTS.put(EntityType.CREEPER, Material.GUNPOWDER);
        MOB_INGREDIENTS.put(EntityType.SPIDER, Material.STRING);
        MOB_INGREDIENTS.put(EntityType.SLIME, Material.SLIME_BALL);
        MOB_INGREDIENTS.put(EntityType.ENDERMAN, Material.ENDER_PEARL);
        MOB_INGREDIENTS.put(EntityType.BLAZE, Material.BLAZE_ROD);
        MOB_INGREDIENTS.put(EntityType.MAGMA_CUBE, Material.MAGMA_CREAM);
        MOB_INGREDIENTS.put(EntityType.WITCH, Material.GLASS_BOTTLE);
        MOB_INGREDIENTS.put(EntityType.VILLAGER, Material.EMERALD);
        MOB_INGREDIENTS.put(EntityType.IRON_GOLEM, Material.IRON_INGOT);
        MOB_INGREDIENTS.put(EntityType.SNOW_GOLEM, Material.SNOWBALL);
        MOB_INGREDIENTS.put(EntityType.WOLF, Material.BONE);
        MOB_INGREDIENTS.put(EntityType.CAT, Material.STRING);
        MOB_INGREDIENTS.put(EntityType.PIG, Material.PORKCHOP);
        MOB_INGREDIENTS.put(EntityType.COW, Material.LEATHER);
        MOB_INGREDIENTS.put(EntityType.SHEEP, Material.WHITE_WOOL);
        MOB_INGREDIENTS.put(EntityType.CHICKEN, Material.FEATHER);
        MOB_INGREDIENTS.put(EntityType.HORSE, Material.LEATHER);
        MOB_INGREDIENTS.put(EntityType.RABBIT, Material.RABBIT_HIDE);
        MOB_INGREDIENTS.put(EntityType.BEE, Material.HONEYCOMB);
        MOB_INGREDIENTS.put(EntityType.SILVERFISH, Material.STONE);
        MOB_INGREDIENTS.put(EntityType.ZOMBIE_VILLAGER, Material.ROTTEN_FLESH);
        MOB_INGREDIENTS.put(EntityType.HUSK, Material.SAND);
        MOB_INGREDIENTS.put(EntityType.STRAY, Material.SNOWBALL);
        MOB_INGREDIENTS.put(EntityType.DROWNED, Material.KELP);
        MOB_INGREDIENTS.put(EntityType.PILLAGER, Material.CROSSBOW);
        MOB_INGREDIENTS.put(EntityType.VINDICATOR, Material.IRON_AXE);
        MOB_INGREDIENTS.put(EntityType.EVOKER, Material.TOTEM_OF_UNDYING);
        MOB_INGREDIENTS.put(EntityType.GHAST, Material.GHAST_TEAR);
        MOB_INGREDIENTS.put(EntityType.ZOMBIFIED_PIGLIN, Material.GOLD_NUGGET);
        MOB_INGREDIENTS.put(EntityType.HOGLIN, Material.LEATHER);
        MOB_INGREDIENTS.put(EntityType.PIGLIN, Material.GOLD_INGOT);
        MOB_INGREDIENTS.put(EntityType.ZOGLIN, Material.ROTTEN_FLESH);
        MOB_INGREDIENTS.put(EntityType.FOX, Material.SWEET_BERRIES);
        MOB_INGREDIENTS.put(EntityType.PANDA, Material.BAMBOO);
        MOB_INGREDIENTS.put(EntityType.BAT, Material.COAL);
        MOB_INGREDIENTS.put(EntityType.SNIFFER, Material.WHEAT_SEEDS);
        MOB_INGREDIENTS.put(EntityType.AXOLOTL, Material.TROPICAL_FISH);
        MOB_INGREDIENTS.put(EntityType.GOAT, Material.MILK_BUCKET);
        MOB_INGREDIENTS.put(EntityType.TURTLE, Material.SEAGRASS);
    }

    public SpawnEggRecipeManager(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    public void registerAllSpawnEggs() {
        for (EntityType type : EntityType.values()) {
            if (!type.isAlive() || !type.isSpawnable() || blocked.contains(type)) continue;

            try {
                registerRecipe(type);
            } catch (Exception ignored) {
                // Kein Spawn-Ei vorhanden -> überspringen
            }
        }
    }

    private void registerRecipe(EntityType entityType) {
        Material ingredient = MOB_INGREDIENTS.getOrDefault(entityType, Material.GUNPOWDER);

        ItemStack result = new ItemStack(Material.valueOf(entityType.name() + "_SPAWN_EGG"));
        ItemMeta meta = result.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(ChatColor.GRAY + "Spawn-Ei: " + entityType.name());
            result.setItemMeta(meta);
        }

        NamespacedKey key = new NamespacedKey(plugin, "spawn_egg_" + entityType.name().toLowerCase());
        ShapedRecipe recipe = new ShapedRecipe(key, result);
        recipe.shape("GGG", "GEG", "GGG");
        recipe.setIngredient('G', ingredient);
        recipe.setIngredient('E', Material.EGG);

        Bukkit.addRecipe(recipe);
    }
}
