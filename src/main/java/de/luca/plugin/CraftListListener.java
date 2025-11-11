package de.luca.plugin;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;

/* Schlanker Wrapper: Nur die statische handleClick wird vom Router aufgerufen.
   (Eigene Listener-Registrierung ist NICHT nötig.) */
public class CraftListListener {

    public static void handleClick(LucaCrafterPlugin plugin, Player p, InventoryClickEvent e) {
        // Weitergereicht an CraftListGUI (Lesbarkeit)
        CraftListGUI.handleClick(plugin, p, e);
    }
}
