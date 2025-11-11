package de.luca.plugin;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.*;

public class CraftGUIListener implements Listener {

    private final LucaCrafterPlugin plugin;

    public CraftGUIListener(LucaCrafterPlugin plugin) {
        this.plugin = plugin;
    }

    /* Zentraler Router: ruft die Handler der einzelnen GUIs auf */
    @EventHandler
    public void onInventoryClick(InventoryClickEvent e) {
        if (!(e.getWhoClicked() instanceof Player p)) return;

        // Reihenfolge wichtig: erst Menüs, dann Editor
        CraftMainGUI.handleClick(plugin, p, e);
        CraftListGUI.handleClick(plugin, p, e);
        CraftEditorGUI.handleClick(plugin, p, e);
    }

    /* Shift-Klicken/Hotbar-Swap in unseren GUIs komplett unterbinden */
    @EventHandler
    public void onInventoryDrag(InventoryDragEvent e) {
        if (!(e.getWhoClicked() instanceof Player p)) return;
        if (CraftMainGUI.isThisGUI(e.getView()) ||
            CraftListGUI.isThisGUI(e.getView()) ||
            CraftEditorGUI.isThisGUI(e.getView())) {
            e.setCancelled(true);
        }
    }
}
