package de.luca.plugin;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.UUID;

/**
 * Aktualisiert Prefix im Chat, in der Tablist und über dem Kopf.
 * Priorität:
 * 1. Custom-Prefix (falls aktiviert)
 * 2. Rollen-Prefix aus RoleManager
 */
public class PrefixUpdater {

    private final LucaCrafterPlugin plugin;
    private final RoleManager roleManager;
    private final PlayerPrefixManager prefixManager;

    private final LegacyComponentSerializer legacy = LegacyComponentSerializer.legacyAmpersand();

    public PrefixUpdater(LucaCrafterPlugin plugin, RoleManager roleManager, PlayerPrefixManager prefixManager) {
        this.plugin = plugin;
        this.roleManager = roleManager;
        this.prefixManager = prefixManager;
    }

    private String color(String s) {
        return ChatColor.translateAlternateColorCodes('&', s);
    }

    public void update(Player player) {
        UUID uuid = player.getUniqueId();

        // 1) Custom-Prefix?
        String customPrefix = prefixManager.getFormattedCustomPrefix(uuid);
        boolean customEnabled = prefixManager.isCustomEnabled(uuid);

        String activePrefix;
        if (customEnabled && !customPrefix.isEmpty()) {
            activePrefix = customPrefix;
        } else {
            // 2) sonst Rollen-Prefix
            String role = roleManager.getRole(uuid);
            String rolePrefix = roleManager.getPrefix(role);
            if (rolePrefix == null) rolePrefix = "";
            if (!rolePrefix.isEmpty()) {
                if (!rolePrefix.endsWith(" ")) {
                    rolePrefix = rolePrefix + " ";
                }
                activePrefix = rolePrefix;
            } else {
                activePrefix = "";
            }
        }

        String coloredPrefix = color(activePrefix);
        String fullName = coloredPrefix + player.getName();

        // alter String-Name (für Plugins, die das brauchen)
        player.setDisplayName(fullName);
        player.setPlayerListName(fullName);

        // Adventure-Namen (Chat + Kopf + Tablist)
        Component comp = legacy.deserialize(activePrefix + player.getName());
        player.displayName(comp);        // Chat
        player.playerListName(comp);     // Tablist
        player.customName(comp);         // über dem Kopf
        player.setCustomNameVisible(true);
    }

    public void updateAll() {
        for (Player p : Bukkit.getOnlinePlayers()) {
            update(p);
        }
    }
}
