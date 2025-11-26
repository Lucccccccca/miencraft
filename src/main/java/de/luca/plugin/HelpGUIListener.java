package de.luca.plugin;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;

public class HelpGUIListener implements Listener {

    private final LucaCrafterPlugin plugin;

    public HelpGUIListener(LucaCrafterPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onClick(InventoryClickEvent e) {

        if (e.getCurrentItem() == null || e.getCurrentItem().getItemMeta() == null) return;

        Player p = (Player) e.getWhoClicked();
        String title = e.getView().getTitle();
        String name = ChatColor.stripColor(e.getCurrentItem().getItemMeta().getDisplayName());

        // ❗ Nur unsere GUIs blockieren!
        boolean isHelpGUI =
                title.equals("§e📘 Hilfe-Menü") ||
                title.equals("§6📦 Plugin-Info") ||        // <-- FIXED TITLE!
                title.startsWith("§e📘 Kategorie:") ||
                title.startsWith("§b📜 Changelog") ||
                title.startsWith("§b📜 Version ");

        if (!isHelpGUI) return;

        e.setCancelled(true);

        // === HAUPTMENÜ ===
        if (title.equals("§e📘 Hilfe-Menü")) {

            if (name.contains("Plugin-Info")) {
                p.openInventory(PluginInfoGUI.build(plugin, p));
                return;
            }

            for (HelpCategory cat : HelpCategory.values()) {
                if (name.equalsIgnoreCase(ChatColor.stripColor(cat.getDisplay()))) {
                    p.openInventory(HelpCategoryGUI.build(plugin, cat, p));
                    return;
                }
            }
        }

        // ZURÜCK
        if (name.equalsIgnoreCase("← Zurück")) {
            p.openInventory(HelpMainGUI.build(plugin, p));
        }
    }
}
