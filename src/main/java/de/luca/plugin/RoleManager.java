package de.luca.plugin;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class RoleManager {

    private final LucaCrafterPlugin plugin;

    private final Map<String, Set<String>> rolePermissions = new LinkedHashMap<>();
    private final Map<UUID, String> playerRoles = new HashMap<>();
    private final Map<String, String> rolePrefixes = new HashMap<>();

    private File rolesFile;
    private FileConfiguration rolesCfg;

    public RoleManager(LucaCrafterPlugin plugin) {
        this.plugin = plugin;

        rolesFile = new File(plugin.getDataFolder(), "roles.yml");
        if (!rolesFile.exists()) {
            plugin.saveResource("roles.yml", false);
        }

        load();
        ensureDefaults();
        save();
    }

    // ============================================================
    // ================   LOAD & SAVE SYSTEM    ====================
    // ============================================================

    /** Lädt roles.yml neu */
    private void load() {
        rolesCfg = YamlConfiguration.loadConfiguration(rolesFile);

        rolePermissions.clear();
        playerRoles.clear();
        rolePrefixes.clear();

        // Rollen & Permissions laden
        if (rolesCfg.isConfigurationSection("roles")) {
            for (String role : rolesCfg.getConfigurationSection("roles").getKeys(false)) {
                List<String> perms = rolesCfg.getStringList("roles." + role);
                rolePermissions.put(role, new HashSet<>(perms));
            }
        }

        // Spielerrollen laden
        if (rolesCfg.isConfigurationSection("players")) {
            for (String uuidStr : rolesCfg.getConfigurationSection("players").getKeys(false)) {
                UUID uuid = UUID.fromString(uuidStr);
                String role = rolesCfg.getString("players." + uuidStr);
                playerRoles.put(uuid, role);
            }
        }

        // Prefixe laden
        if (rolesCfg.isConfigurationSection("prefixes")) {
            for (String role : rolesCfg.getConfigurationSection("prefixes").getKeys(false)) {
                rolePrefixes.put(role, rolesCfg.getString("prefixes." + role, ""));
            }
        }
    }

    /** Schreibt alle Daten in roles.yml */
    public void save() {
        try {
            // Permissions speichern
            for (String role : rolePermissions.keySet()) {
                rolesCfg.set("roles." + role, new ArrayList<>(rolePermissions.get(role)));
            }

            // Spielerrollen speichern
            for (UUID uuid : playerRoles.keySet()) {
                rolesCfg.set("players." + uuid.toString(), playerRoles.get(uuid));
            }

            // Prefixe speichern
            for (String role : rolePrefixes.keySet()) {
                rolesCfg.set("prefixes." + role, rolePrefixes.get(role));
            }

            rolesCfg.save(rolesFile);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // ============================================================
    // ============       DEFAULTS ERSTELLEN        ===============
    // ============================================================

    public void ensureDefaults() {

        if (!rolePermissions.containsKey("spieler")) {
            rolePermissions.put("spieler", new HashSet<>(Arrays.asList(
                    "lucacrafter.baum.use",
                    "lucacrafter.erz.use",
                    "lucacrafter.settings.open"
            )));
        }

        if (!rolePermissions.containsKey("vip"))
            rolePermissions.put("vip", new HashSet<>());

        if (!rolePermissions.containsKey("moderator"))
            rolePermissions.put("moderator", new HashSet<>(Arrays.asList(
                    "lucacrafter.perms.manage"
            )));

        if (!rolePermissions.containsKey("admin"))
            rolePermissions.put("admin", new HashSet<>(Collections.singletonList("*")));

        // Prefixes defaults
        if (!rolePrefixes.containsKey("spieler"))
            rolePrefixes.put("spieler", "");

        if (!rolePrefixes.containsKey("vip"))
            rolePrefixes.put("vip", "[VIP] ");

        if (!rolePrefixes.containsKey("moderator"))
            rolePrefixes.put("moderator", "[Mod] ");

        if (!rolePrefixes.containsKey("admin"))
            rolePrefixes.put("admin", "[Admin] ");
    }

    // ============================================================
    // ====================   GETTER / SETTER   ===================
    // ============================================================

    public String getRole(UUID uuid) {
        return playerRoles.getOrDefault(uuid, "spieler");
    }

    public void setRole(UUID uuid, String role) {
        playerRoles.put(uuid, role);
        save();
    }

    public Set<String> getRolePermissions(String role) {
        return rolePermissions.getOrDefault(role, Collections.emptySet());
    }

    public String getPrefix(String role) {
        return rolePrefixes.getOrDefault(role, "");
    }

    /**
     * Wird z.B. im PermissionFinder benutzt.
     */
    public Set<String> allRoles() {
        return new LinkedHashSet<>(rolePermissions.keySet());
    }

    // ============================================================
    // ===================   PERMISSION SYSTEM   ==================
    // ============================================================

    public void addPermissionToRole(String role, String permission) {
        rolePermissions.computeIfAbsent(role, k -> new HashSet<>()).add(permission);
        save();

        plugin.getPermissionUpdater().refreshAllPlayers();
        plugin.getPrefixUpdater().updateAll();
    }

    public void removePermissionFromRole(String role, String permission) {
        if (!rolePermissions.containsKey(role)) return;
        rolePermissions.get(role).remove(permission);
        save();

        plugin.getPermissionUpdater().refreshAllPlayers();
        plugin.getPrefixUpdater().updateAll();
    }

    // ============================================================
    // ===================   RELOAD SYSTEM   ======================
    // ============================================================

    /** 🔥 Rollen, Prefixe und Permissions live neu laden */
    public void reloadRoles() {
        load();
        ensureDefaults();
        save();

        plugin.getPermissionUpdater().refreshAllPlayers();
        plugin.getPrefixUpdater().updateAll();

        plugin.getLogger().info("🔄 Rollen, Prefixe & Permissions erfolgreich neu geladen!");
    }

    // ============================================================
    // ================   API für GUIs / Commands  =================
    // ============================================================

    /**
     * Wird von GUIs benutzt – Liste aller Rollennamen.
     */
    public List<String> getAllRoles() {
        return new ArrayList<>(rolePermissions.keySet());
    }

    /**
     * Neue Rolle anlegen (ohne Rechte, Prefix = "")
     * @return false, wenn die Rolle schon existiert
     */
    public boolean createRole(String roleName) {
        if (roleName == null || roleName.trim().isEmpty()) return false;

        roleName = roleName.toLowerCase(Locale.ROOT);

        if (rolePermissions.containsKey(roleName)) {
            return false; // gibt’s schon
        }

        rolePermissions.put(roleName, new HashSet<>());
        rolePrefixes.putIfAbsent(roleName, "");

        save();
        plugin.getPermissionUpdater().refreshAllPlayers();
        plugin.getPrefixUpdater().updateAll();
        return true;
    }

    /**
     * Rolle löschen. Spieler mit dieser Rolle bekommen automatisch "spieler".
     * Standardrollen können (zur Sicherheit) nicht gelöscht werden.
     * @return false, wenn Rolle nicht existiert oder geschützt ist.
     */
    public boolean deleteRole(String roleName) {
        if (roleName == null) return false;

        roleName = roleName.toLowerCase(Locale.ROOT);

        // Standardrollen schützen
        if (roleName.equals("spieler") || roleName.equals("admin") ||
                roleName.equals("moderator") || roleName.equals("vip")) {
            return false;
        }

        if (!rolePermissions.containsKey(roleName)) {
            return false;
        }

        // Rolle aus Maps entfernen
        rolePermissions.remove(roleName);
        rolePrefixes.remove(roleName);

        // Spieler, die diese Rolle haben, auf "spieler" zurücksetzen
        for (Map.Entry<UUID, String> entry : new HashMap<>(playerRoles).entrySet()) {
            if (roleName.equalsIgnoreCase(entry.getValue())) {
                playerRoles.put(entry.getKey(), "spieler");
            }
        }

        save();
        plugin.getPermissionUpdater().refreshAllPlayers();
        plugin.getPrefixUpdater().updateAll();
        return true;
    }
}
