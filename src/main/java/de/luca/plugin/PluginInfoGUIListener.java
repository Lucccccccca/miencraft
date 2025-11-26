package de.luca.plugin;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;

public class PluginInfoGUIListener implements Listener {

    private final LucaCrafterPlugin plugin;

    public PluginInfoGUIListener(LucaCrafterPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onClick(InventoryClickEvent e) {

        if (e.getCurrentItem() == null || e.getCurrentItem().getItemMeta() == null)
            return;

        Player p = (Player) e.getWhoClicked();
        String title = ChatColor.stripColor(e.getView().getTitle());
        String name = ChatColor.stripColor(e.getCurrentItem().getItemMeta().getDisplayName());

        // ❗ Nur blockieren, wenn es WIRKLICH unser Plugin-Info GUI ist
        boolean isInfo =
                title.equalsIgnoreCase("📦 Plugin-Info") ||
                title.equalsIgnoreCase("📝 Changelog") ||
                title.startsWith("📜 Version ");

        if (!isInfo) return; // Spielerinventar ist NICHT betroffen!

        e.setCancelled(true);

        // =========================
        // PLUGIN INFO GUI
        // =========================
        if (title.equals("📦 Plugin-Info")) {

            if (name.equals("📝 Changelog")) {
                p.openInventory(ChangelogListGUI.build(p));
                return;
            }

            if (name.equals("← Zurück")) {
                p.openInventory(HelpMainGUI.build(plugin, p));
                return;
            }
        }

        // =========================
        // CHANGELOG LISTE
        // =========================
        if (title.equals("📝 Changelog")) {

            if (name.equals("← Zurück")) {
                p.openInventory(PluginInfoGUI.build(plugin, p));
                return;
            }

            // Version Details öffnen
            if (ChangelogListGUI.CHANGELOG.containsKey(name)) {
                String[] details = ChangelogListGUI.CHANGELOG.get(name);
                p.openInventory(ChangelogDetailGUI.build(name, details));
            }
        }
    }
}
