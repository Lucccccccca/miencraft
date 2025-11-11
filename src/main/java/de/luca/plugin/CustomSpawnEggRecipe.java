package de.luca.plugin;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.meta.SpawnEggMeta;
import org.bukkit.plugin.java.JavaPlugin;

public class CustomSpawnEggRecipe {

    private final JavaPlugin plugin;

    public CustomSpawnEggRecipe(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    public void registerAll() {
        // Beispiel: Creeper-, Zombie-, Skeleton-Ei
        registerSpawnEggRecipe(EntityType.CREEPER, Material.GUNPOWDER);
        registerSpawnEggRecipe(EntityType.ZOMBIE, Material.ROTTEN_FLESH);
        registerSpawnEggRecipe(EntityType.SKELETON, Material.BONE);
        registerSpawnEggRecipe(EntityType.SPIDER, Material.STRING);
    }

    private void registerSpawnEggRecipe(EntityType type, Material centerMaterial) {
        try {
            ItemStack egg = new ItemStack(Material.CREEPER_SPAWN_EGG); // Dummy
            SpawnEggMeta meta = (SpawnEggMeta) egg.getItemMeta();
            meta.setSpawnedType(type);
            egg.setItemMeta(meta);

            NamespacedKey key = new NamespacedKey(plugin, type.name().toLowerCase() + "_egg");

            ShapedRecipe recipe = new ShapedRecipe(key, egg);
            recipe.shape("GGG", "GEG", "GGG");
            recipe.setIngredient('G', Material.GUNPOWDER);
            recipe.setIngredient('E', centerMaterial);

            Bukkit.addRecipe(recipe);
            Bukkit.getLogger().info("✅ SpawnEgg-Rezept registriert: " + type.name());
        } catch (Exception e) {
            Bukkit.getLogger().warning("⚠️ Konnte Rezept für " + type + " nicht laden: " + e.getMessage());
        }
    }
}
