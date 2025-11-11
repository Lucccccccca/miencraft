package de.luca.plugin;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.*;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;

public class CraftListGUI {

    public static final String TITLE = "§b📘 Gespeicherte Rezepte";
    private final LucaCrafterPlugin plugin;

    public CraftListGUI(LucaCrafterPlugin plugin) {
        this.plugin = plugin;
    }

    public void open(Player p) {
        List<ItemStack> results = plugin.getRecipeStorage().getResultItems();
        int rows = Math.min(6, Math.max(1, (results.size() + 8) / 9));
        Inventory inv = Bukkit.createInventory(null, rows * 9, TITLE);

        ItemStack filler = named(Material.GRAY_STAINED_GLASS_PANE, " ");
        for (int i = 0; i < inv.getSize(); i++) inv.setItem(i, filler);

        int slot = 0;
        for (ItemStack r : results) {
            if (slot >= inv.getSize() - 1) break;
            inv.setItem(slot++, r);
        }

        inv.setItem(inv.getSize() - 1, named(Material.ARROW, "§7Zurück"));

        p.openInventory(inv);
    }

    public static boolean isThisGUI(InventoryView view) {
        return view != null && TITLE.equals(view.getTitle());
    }

    public static void handleClick(LucaCrafterPlugin plugin, Player p, InventoryClickEvent e) {
        if (!isThisGUI(e.getView())) return;
        e.setCancelled(true);
        if (e.getClickedInventory() == null) return;

        Inventory inv = e.getView().getTopInventory();
        int slot = e.getRawSlot();

        // Zurück-Button
        if (slot == inv.getSize() - 1) {
            new CraftMainGUI(plugin).open(p);
            return;
        }

        // Klick auf Rezept
        ItemStack clicked = e.getCurrentItem();
        if (clicked == null || clicked.getType() == Material.AIR) return;

        // Rechtsklick = Löschen
        if (e.isRightClick()) {
            String itemName = clicked.getType().name();
            boolean removed = plugin.getRecipeStorage().removeRecipeByResult(clicked);

            if (removed) {
                p.sendMessage("§c🗑 Rezept §e" + itemName + " §cwurde gelöscht!");
                p.playSound(p.getLocation(), Sound.ENTITY_ITEM_BREAK, 1f, 1f);
                new CraftListGUI(plugin).open(p); // GUI neu laden
            } else {
                p.sendMessage("§7⚠ Rezept konnte nicht gefunden werden.");
            }
        } 
        // Linksklick = anzeigen (nichts passiert, nur Ton)
        else if (e.isLeftClick()) {
            p.playSound(p.getLocation(), Sound.UI_BUTTON_CLICK, 0.8f, 1.2f);
        }
    }

    private static ItemStack named(Material m, String name) {
        ItemStack it = new ItemStack(m);
        ItemMeta meta = it.getItemMeta();
        meta.setDisplayName(name);
        it.setItemMeta(meta);
        return it;
    }
}
