package de.luca.plugin;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Arrays;
import java.util.UUID;

public class PrefixGUI {

    public static final String TITLE = "§aPrefix-Einstellungen";

    public static final int SLOT_TOGGLE = 11;
    public static final int SLOT_TEXT   = 13;
    public static final int SLOT_COLOR  = 15;
    public static final int SLOT_RESET  = 22;

    public static void open(LucaCrafterPlugin plugin, Player player) {
        PlayerPrefixManager pm = plugin.getPlayerPrefixManager();
        UUID uuid = player.getUniqueId();

        boolean enabled = pm.isCustomEnabled(uuid);
        String text = pm.getCustomText(uuid);
        if (text == null) text = "";

        Inventory inv = Bukkit.createInventory(null, 27, TITLE);

        // Hintergrund
        ItemStack glass = createItem(Material.GRAY_STAINED_GLASS_PANE, " ");
        for (int i = 0; i < inv.getSize(); i++) {
            inv.setItem(i, glass);
        }

        // Toggle
        Material toggleMat = enabled ? Material.LIME_DYE : Material.GRAY_DYE;
        String toggleName = enabled ? "§aCustom-Prefix: §2AKTIV" : "§aCustom-Prefix: §cDEAKTIVIERT";
        ItemStack toggle = createItem(toggleMat, toggleName,
                "§7Schalte deinen eigenen Prefix",
                "§7ein oder aus.");
        inv.setItem(SLOT_TOGGLE, toggle);

        // Text ändern
        ItemStack textItem = createItem(Material.PAPER, "§ePrefix-Text ändern",
                "§7Aktuell: §f" + (text.isEmpty() ? "§o(keiner)" : text),
                " ",
                "§eKlicke§7, um den Prefix",
                "§7im Chat einzugeben."
        );
        inv.setItem(SLOT_TEXT, textItem);

        // Farbe wählen
        ItemStack colorItem = createItem(Material.RED_DYE, "§bFarbe wählen",
                "§7Wähle eine Farbe,",
                "§7die vor dem Prefix steht.");
        inv.setItem(SLOT_COLOR, colorItem);

        // Reset
        ItemStack resetItem = createItem(Material.BARRIER, "§cReset auf Rollen-Prefix",
                "§7Deaktiviert deinen Custom-Prefix",
                "§7und nutzt wieder den Rollen-Prefix.");
        inv.setItem(SLOT_RESET, resetItem);

        player.openInventory(inv);
    }

    public static ItemStack createItem(Material mat, String name, String... lore) {
        ItemStack item = new ItemStack(mat);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(name);
        if (lore != null && lore.length > 0) {
            meta.setLore(Arrays.asList(lore));
        }
        item.setItemMeta(meta);
        return item;
    }
}
