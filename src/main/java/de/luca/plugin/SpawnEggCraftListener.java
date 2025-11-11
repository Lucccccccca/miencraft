package de.luca.plugin;

import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.inventory.CraftingInventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;

public class SpawnEggCraftListener implements Listener {

    private final NamespacedKey eggTypeKey;

    public SpawnEggCraftListener(JavaPlugin plugin) {
        this.eggTypeKey = new NamespacedKey(plugin, "spawn_egg_type");
    }

    @EventHandler
    public void onPrepareCraft(PrepareItemCraftEvent e) {
        CraftingInventory inv = e.getInventory();
        ItemStack result = inv.getResult();
        if (result == null || result.getType() != Material.CLAY_BALL) return;

        ItemMeta meta = result.getItemMeta();
        if (meta == null) return;

        String entityName = meta.getPersistentDataContainer().get(eggTypeKey, PersistentDataType.STRING);
        if (entityName == null) return;

        try {
            EntityType type = EntityType.valueOf(entityName);
            Material spawnMat = Material.valueOf(type.name() + "_SPAWN_EGG");
            inv.setResult(new ItemStack(spawnMat)); // echtes Spawn-Ei als Craft-Ergebnis
        } catch (IllegalArgumentException ignored) {
            // Ungültiger Typ / Material nicht vorhanden -> nix ersetzen
        }
    }
}
