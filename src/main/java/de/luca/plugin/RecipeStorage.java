package de.luca.plugin;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.RecipeChoice;
import org.bukkit.inventory.ShapedRecipe;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.lang.reflect.Type;
import java.util.*;

public class RecipeStorage {

    private final LucaCrafterPlugin plugin;
    private final List<ShapedRecipe> recipes = new ArrayList<>();
    private final File file;
    private final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    public RecipeStorage(LucaCrafterPlugin plugin) {
        this.plugin = plugin;
        this.file = new File(plugin.getDataFolder(), "recipes.json");
        loadRecipes();
    }

    public void addRecipe(ShapedRecipe recipe) {
        recipes.add(recipe);
        saveRecipes();
    }

    public void openRecipeList(Player p) {
        Inventory inv = Bukkit.createInventory(null, 54, "§b📘 Gespeicherte Rezepte");
        int slot = 0;
        for (ShapedRecipe recipe : recipes) {
            inv.setItem(slot++, recipe.getResult());
            if (slot >= 54) break;
        }
        if (recipes.isEmpty()) {
            ItemStack barrier = new ItemStack(Material.BARRIER);
            inv.setItem(22, barrier);
        }
        p.openInventory(inv);
    }

    private void saveRecipes() {
        try (FileWriter writer = new FileWriter(file)) {
            List<Map<String, Object>> data = new ArrayList<>();
            for (ShapedRecipe recipe : recipes) {
                Map<String, Object> entry = new HashMap<>();
                entry.put("key", recipe.getKey().getKey());
                entry.put("shape", recipe.getShape());
                Map<String, String> ingredients = new HashMap<>();
                for (var ingredient : recipe.getChoiceMap().entrySet()) {
                    if (ingredient.getValue() instanceof RecipeChoice.MaterialChoice matChoice) {
                        Material mat = matChoice.getChoices().iterator().next();
                        ingredients.put(ingredient.getKey().toString(), mat.name());
                    }
                }
                entry.put("ingredients", ingredients);
                entry.put("result", recipe.getResult().getType().name());
                data.add(entry);
            }
            gson.toJson(data, writer);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public List<ItemStack> getResultItems() {
    List<ItemStack> results = new ArrayList<>();
    for (Recipe recipe : recipes) {
        if (recipe instanceof ShapedRecipe shaped) {
            results.add(shaped.getResult());
        }
    }
    return results;
}

    public boolean removeRecipeByResult(ItemStack result) {
    ShapedRecipe toRemove = null;
    for (ShapedRecipe r : recipes) {
        if (r.getResult().getType() == result.getType()) {
            toRemove = r;
            break;
        }
    }
    if (toRemove != null) {
        recipes.remove(toRemove);
        saveRecipes();
        Bukkit.removeRecipe(toRemove.getKey());
        return true;
    }
    return false;
}


    private void loadRecipes() {
        if (!file.exists()) return;
        try (FileReader reader = new FileReader(file)) {
            Type listType = new TypeToken<List<Map<String, Object>>>() {}.getType();
            List<Map<String, Object>> data = gson.fromJson(reader, listType);
            if (data == null) return;

            for (Map<String, Object> entry : data) {
                String key = (String) entry.get("key");
                List<String> shape = (List<String>) entry.get("shape");
                Map<String, String> ingredients = (Map<String, String>) entry.get("ingredients");
                String resultName = (String) entry.get("result");

                Material resultMat = Material.matchMaterial(resultName);
                if (resultMat == null) continue;

                ShapedRecipe recipe = new ShapedRecipe(new NamespacedKey(plugin, key), new ItemStack(resultMat));
                recipe.shape(shape.toArray(new String[0]));
                for (Map.Entry<String, String> ingr : ingredients.entrySet()) {
                    Material mat = Material.matchMaterial(ingr.getValue());
                    if (mat != null)
                        recipe.setIngredient(ingr.getKey().charAt(0), new RecipeChoice.MaterialChoice(mat));
                }

                Bukkit.addRecipe(recipe);
                recipes.add(recipe);
            }
            plugin.getLogger().info("✅ " + recipes.size() + " Custom-Rezepte geladen.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
