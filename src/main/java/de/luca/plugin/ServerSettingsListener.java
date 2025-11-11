package de.luca.plugin;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;

public class ServerSettingsListener implements Listener {

    private final LucaCrafterPlugin plugin;

    public ServerSettingsListener(LucaCrafterPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent e) {
        if (!(e.getWhoClicked() instanceof Player p)) return;
        if (e.getView().getTitle() == null) return;
        if (e.getCurrentItem() == null) return;

        if (!e.getView().getTitle().equals("§b⚙️ Server-Einstellungen")) return;
        e.setCancelled(true);

        if (!p.isOp()) {
            p.sendMessage("§cNur Admins dürfen Server-Einstellungen ändern!");
            p.closeInventory();
            return;
        }

        Material type = e.getCurrentItem().getType();

        switch (type) {
            case WHEAT -> plugin.getConfigManager().setFarmProtectEnabled(!plugin.getConfigManager().isFarmProtectEnabled());
            case CREEPER_HEAD -> plugin.getConfigManager().setAntiCreeperEnabled(!plugin.getConfigManager().isAntiCreeperEnabled());
            case TNT -> plugin.getConfigManager().setTntBlockDamageEnabled(!plugin.getConfigManager().isTntBlockDamageEnabled());
            case IRON_SWORD -> plugin.getConfigManager().setPvpEnabled(!plugin.getConfigManager().isPvpEnabled());
            case ZOMBIE_HEAD -> plugin.getConfigManager().setMobGriefingEnabled(!plugin.getConfigManager().isMobGriefingEnabled());
            case BARRIER -> p.closeInventory();
        }

        p.sendMessage("§a⚙️ Server-Einstellungen aktualisiert!");
        new ServerSettingsGUI(p, plugin).open(); // Menü neu öffnen mit aktualisierten Werten
    }
}
