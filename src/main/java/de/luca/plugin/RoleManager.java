package de.luca.plugin;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.*;

/**
 * Verwalten von Rollen, deren Permissions und Zuordnung zu Spielern.
 * Ruft nach Änderungen den PermissionUpdater auf, um die Berechtigungen zu aktualisieren.
 */
public class RoleManager {

    private final LucaCrafterPlugin plugin;
    private final File file;
    private final FileConfiguration cfg;

    // Caches
    private final Map<String, Set<String>> rolePerms = new LinkedHashMap<>();
    private final Map<UUID, String> playerRoles = new HashMap<>();

    public RoleManager(LucaCrafterPlugin plugin) {
        this.plugin = plugin;
        this.file = new File(plugin.getDataFolder(), "roles.yml");
        this.cfg = YamlConfiguration.loadConfiguration(file);

        load();
        ensureDefaults();
        save();
        plugin.getLogger().info("✅ Rollen geladen: " + getAllRoles());
    }

    private void ensureDefaults() {
        // Standardrollen definieren
        rolePerms.putIfAbsent("spieler", new HashSet<>(List.of(
                "lucacrafter.craftgui.use",
                "lucacrafter.stats.use",
                "lucacrafter.baum.use",
                "lucacrafter.erz.use",
                "lucacrafter.afk.use"
        )));
        rolePerms.putIfAbsent("vip", new HashSet<>(List.of(
                "lucacrafter.autopickup",
                "lucacrafter.magnet",
                "lucacrafter.fastfurnace"
        )));
        rolePerms.putIfAbsent("moderator", new HashSet<>(List.of(
                "lucacrafter.perms.manage",
                "lucacrafter.settings.open",
                "lucacrafter.server.open"
        )));
        rolePerms.putIfAbsent("admin", new HashSet<>(List.of(
                "lucacrafter.perms.manage",
                "lucacrafter.antigrief.toggle",
                "lucacrafter.settings.open",
                "lucacrafter.server.open",
                "lucacrafter.server.restart"
        )));
    }

    private void load() {
        // Rollen
        if (cfg.isConfigurationSection("roles")) {
            for (String role : cfg.getConfigurationSection("roles").getKeys(false)) {
                Set<String> set = new HashSet<>(cfg.getStringList("roles." + role));
                rolePerms.put(role, set);
            }
        }
        // Spieler→Rolle
        if (cfg.isConfigurationSection("players")) {
            for (String key : cfg.getConfigurationSection("players").getKeys(false)) {
                try {
                    UUID id = UUID.fromString(key);
                    String role = cfg.getString("players." + key, "spieler");
                    playerRoles.put(id, role);
                } catch (IllegalArgumentException ignored) {}
            }
        }
    }

    public void save() {
        cfg.set("roles", null);
        for (Map.Entry<String, Set<String>> e : rolePerms.entrySet()) {
            cfg.set("roles." + e.getKey(), new ArrayList<>(e.getValue()));
        }
        cfg.set("players", null);
        for (Map.Entry<UUID, String> e : playerRoles.entrySet()) {
            cfg.set("players." + e.getKey(), e.getValue());
        }
        try {
            cfg.save(file);
        } catch (IOException e) {
            plugin.getLogger().severe("Konnte roles.yml nicht speichern: " + e.getMessage());
        }
    }

    /* ================= API ================= */

    public List<String> getAllRoles() {
        return new ArrayList<>(rolePerms.keySet());
    }

    public boolean createRole(String name) {
        String n = name.toLowerCase(Locale.ROOT).trim();
        if (n.isEmpty() || rolePerms.containsKey(n)) return false;
        rolePerms.put(n, new HashSet<>());
        save();
        plugin.getPermissionUpdater().refreshAllPlayers();
        return true;
    }

    public boolean deleteRole(String name) {
        String n = name.toLowerCase(Locale.ROOT).trim();
        if (!rolePerms.containsKey(n)) return false;
        if (n.equals("spieler")) return false; // baseline behalten
        rolePerms.remove(n);
        // Spieler, die diese Rolle hatten → auf "spieler" zurücksetzen
        for (Map.Entry<UUID, String> e : new HashMap<>(playerRoles).entrySet()) {
            if (e.getValue().equalsIgnoreCase(n)) {
                playerRoles.put(e.getKey(), "spieler");
            }
        }
        save();
        plugin.getPermissionUpdater().refreshAllPlayers();
        return true;
    }

    public Set<String> getRolePermissions(String role) {
        return rolePerms.computeIfAbsent(role, k -> new HashSet<>());
    }

    public void addPermissionToRole(String role, String node) {
        getRolePermissions(role).add(node);
        save();
        plugin.getPermissionUpdater().refreshAllPlayers();
    }

    public void removePermissionFromRole(String role, String node) {
        getRolePermissions(role).remove(node);
        save();
        plugin.getPermissionUpdater().refreshAllPlayers();
    }

    public String getRole(UUID player) {
        return playerRoles.getOrDefault(player, "spieler");
    }

    public void setRole(UUID player, String role) {
        if (!rolePerms.containsKey(role)) role = "spieler";
        playerRoles.put(player, role);
        save();
        plugin.getPermissionUpdater().refreshAllPlayers();
    }

    public boolean roleHas(String role, String node) {
        return getRolePermissions(role).contains(node);
    }

    public boolean playerHas(UUID player, String node) {
        String r = getRole(player);
        return roleHas(r, node);
    }
}
