package de.luca.plugin;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class CraftMainGUI {

    // exakt gleich wie im Screenshot (grün + Icon)
    public static final String TITLE = "§a✳ Custom Crafting";

    private final LucaCrafterPlugin plugin;

    public CraftMainGUI(LucaCrafterPlugin plugin) {
        this.plugin = plugin;
    }

    public void open(Player p) {
        Inventory inv = Bukkit.createInventory(null, 27, TITLE);

        // Füller
        ItemStack filler = new ItemStack(Material.GRAY_STAINED_GLASS_PANE);
        ItemMeta fm = filler.getItemMeta(); 
        fm.setDisplayName(" "); 
        filler.setItemMeta(fm);
        for (int i = 0; i < inv.getSize(); i++) inv.setItem(i, filler);

        inv.setItem(11, named(Material.CRAFTING_TABLE, "§aNeues Rezept erstellen"));
        inv.setItem(15, named(Material.CHEST, "§bGespeicherte Rezepte"));

        p.openInventory(inv);
    }

    public static boolean isThisGUI(org.bukkit.inventory.InventoryView view) {
        return view != null && view.getTitle().equals(TITLE);
    }

    public static void handleClick(LucaCrafterPlugin plugin, Player p, InventoryClickEvent e) {
        if (!isThisGUI(e.getView())) return;

        e.setCancelled(true); // blockt alles standardmäßig
        if (e.getClickedInventory() == null) return;

        switch (e.getRawSlot()) {
            case 11 -> new CraftEditorGUI(plugin).open(p);
            case 15 -> new CraftListGUI(plugin).open(p);
            default -> { /* alles andere ignorieren */ }
        }
    }

    private ItemStack named(Material m, String name) {
        ItemStack it = new ItemStack(m);
        ItemMeta meta = it.getItemMeta();
        meta.setDisplayName(name);
        it.setItemMeta(meta);
        return it;
    }
}
