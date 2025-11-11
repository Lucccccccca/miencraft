package de.luca.plugin;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
    import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.permissions.PermissionAttachment;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Verteilt und aktualisiert Bukkit-Permissions je nach Spieler-Rolle.
 */
public class PermissionUpdater implements Listener {

    private final LucaCrafterPlugin plugin;
    private final Map<UUID, PermissionAttachment> attachments = new HashMap<>();

    public PermissionUpdater(LucaCrafterPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        refreshPermissions(event.getPlayer());
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player p = event.getPlayer();
        PermissionAttachment att = attachments.remove(p.getUniqueId());
        if (att != null) {
            p.removeAttachment(att);
        }
    }

    /**
     * Setzt alle Permissions für einen Spieler basierend auf seiner Rolle.
     */
    public void refreshPermissions(Player p) {
        PermissionAttachment att = attachments.compute(p.getUniqueId(), (uuid, old) -> {
            if (old != null) p.removeAttachment(old);
            return p.addAttachment(plugin);
        });
        // Alte Permissions löschen
        att.getPermissions().clear();
        // Neue setzen
        String role = plugin.getRoleManager().getRole(p.getUniqueId());
        for (String node : plugin.getRoleManager().getRolePermissions(role)) {
            att.setPermission(node, true);
        }
    }

    /**
     * Aktualisiert alle Online-Spieler (z.B. nach Rollentabellen-Änderung).
     */
    public void refreshAllPlayers() {
        for (Player p : Bukkit.getOnlinePlayers()) {
            refreshPermissions(p);
        }
    }
}
