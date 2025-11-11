package de.luca.plugin;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.*;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class CraftEditorGUI {

    public static final String TITLE = "§a🧩 Neues Rezept erstellen";

    // Grid-Mapping (3x3) -> Slots in 45er Inventar
    private static final Map<Character, Integer> GRID = Map.of(
            'A', 10, 'B', 11, 'C', 12,
            'D', 19, 'E', 20, 'F', 21,
            'G', 28, 'H', 29, 'I', 30
    );
    private static final int RESULT_SLOT = 25;
    private static final int SAVE_SLOT   = 40;
    private static final int CANCEL_SLOT = 44;

    private final LucaCrafterPlugin plugin;

    public CraftEditorGUI(LucaCrafterPlugin plugin) {
        this.plugin = plugin;
    }

    public void open(Player p) {
        Inventory inv = Bukkit.createInventory(null, 45, TITLE);

        ItemStack filler = named(Material.GRAY_STAINED_GLASS_PANE, " ");
        for (int i = 0; i < inv.getSize(); i++) inv.setItem(i, filler);

        // Grid-Slots leeren/benutzbar
        for (int slot : GRID.values()) inv.setItem(slot, new ItemStack(Material.AIR));

        // Pfeil und Ergebnis-Slot
        inv.setItem(24, named(Material.ARROW, "§7→"));
        inv.setItem(RESULT_SLOT, new ItemStack(Material.AIR));

        // Buttons
        inv.setItem(SAVE_SLOT, named(Material.LIME_CONCRETE, "§aRezept speichern"));
        inv.setItem(CANCEL_SLOT, named(Material.BARRIER, "§cAbbrechen"));

        p.openInventory(inv);
    }

    public static boolean isThisGUI(InventoryView view) {
        return view != null && TITLE.equals(view.getTitle());
    }

    public static void handleClick(LucaCrafterPlugin plugin, Player p, InventoryClickEvent e) {
        if (!isThisGUI(e.getView())) return;

        // Standard: blocken
        e.setCancelled(true);

        boolean clickTop = e.getClickedInventory() != null && e.getView().getTopInventory().equals(e.getClickedInventory());
        int slot = e.getRawSlot();

        // Erlaube nur Grid-Slots & Ergebnis-Slot in der oberen Hälfte
        if (clickTop) {
            if (GRID.values().contains(slot) || slot == RESULT_SLOT) {
                // In diesen Slots darf man platzieren/entnehmen
                e.setCancelled(false);
            }

            // SAVE
            if (slot == SAVE_SLOT && e.getCurrentItem() != null && e.getCurrentItem().getType() == Material.LIME_CONCRETE) {
                saveRecipe(plugin, p, e.getView().getTopInventory());
            }

            // CANCEL
            if (slot == CANCEL_SLOT) {
                new CraftMainGUI(plugin).open(p);
            }
        } else {
            // Klick im Spieler-Inventar: erlauben, damit man Items aufnehmen kann
            e.setCancelled(false);
        }
    }

    private static void saveRecipe(LucaCrafterPlugin plugin, Player p, Inventory top) {
        ItemStack result = top.getItem(RESULT_SLOT);
        if (result == null || result.getType() == Material.AIR) {
            p.sendMessage("§cBitte lege ein Ergebnis-Item in den rechten Slot.");
            return;
        }

        // Mindestens eine Zutat?
        boolean anyIngredient = GRID.values().stream().map(top::getItem)
                .anyMatch(it -> it != null && it.getType() != Material.AIR);
        if (!anyIngredient) {
            p.sendMessage("§cBitte lege mindestens eine Zutat in die 3×3-Felder.");
            return;
        }

        // Shape immer ABC/DEF/GHI – nicht gesetzte Buchstaben werden schlicht nicht belegt
        ShapedRecipe recipe = new ShapedRecipe(
                new NamespacedKey(plugin, "custom_" + System.currentTimeMillis()),
                result.clone()
        );
        recipe.shape("ABC", "DEF", "GHI");

        // Zutaten zuordnen
        Map<Character, Material> used = new HashMap<>();
        for (Map.Entry<Character, Integer> en : GRID.entrySet()) {
            ItemStack it = top.getItem(en.getValue());
            if (it == null || it.getType() == Material.AIR) continue;
            used.put(en.getKey(), it.getType());
        }

        // Falls nichts gesetzt, abbrechen (sollte oben schon gefiltert sein)
        if (used.isEmpty()) {
            p.sendMessage("§cKeine gültigen Zutaten gefunden.");
            return;
        }

        for (Map.Entry<Character, Material> en : used.entrySet()) {
            recipe.setIngredient(en.getKey(), new RecipeChoice.MaterialChoice(en.getValue()));
        }

        Bukkit.addRecipe(recipe);
        plugin.getRecipeStorage().addRecipe(recipe);

        p.sendMessage("§a✔ Rezept gespeichert!");
        new CraftMainGUI(plugin).open(p);
    }

    private ItemStack named(Material m, String name) {
        ItemStack it = new ItemStack(m);
        ItemMeta meta = it.getItemMeta();
        meta.setDisplayName(name);
        it.setItemMeta(meta);
        return it;
    }
}
