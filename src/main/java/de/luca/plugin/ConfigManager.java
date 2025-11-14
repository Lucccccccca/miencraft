package de.luca.plugin;

import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class ConfigManager {

    private final LucaCrafterPlugin plugin;
    private final File playerDataFolder;
    private final Map<UUID, Boolean> afkStatus = new HashMap<>();

    public ConfigManager(LucaCrafterPlugin plugin) {
        this.plugin = plugin;
        this.playerDataFolder = new File(plugin.getDataFolder(), "playerdata");
        if (!playerDataFolder.exists()) {
            playerDataFolder.mkdirs();
        }

        // Defaults für server.yml
        FileConfiguration cfg = plugin.getConfig();
        cfg.addDefault("server.farm_protect", true);
        cfg.addDefault("server.anti_creeper", true);
        cfg.addDefault("server.tnt_block_damage", true);
        cfg.addDefault("server.pvp", true);
        cfg.addDefault("server.mob_griefing", true);

        // Home-Defaults
        cfg.addDefault("home.default.maxhomes", 10);
        cfg.addDefault("home.default.delay", 3);
        cfg.addDefault("home.default.cooldown", 0);
        cfg.addDefault("home.default.instant", false);
        cfg.addDefault("home.default.movecancel", true);
        cfg.addDefault("home.default.particles", true);

        cfg.options().copyDefaults(true);
        plugin.saveConfig();
    }

    // =============================
    // File Handling
    // =============================

    private File getPlayerFile(UUID uuid) {
        return new File(playerDataFolder, uuid.toString() + ".yml");
    }

    private FileConfiguration getPlayerConfig(UUID uuid) {
        File f = getPlayerFile(uuid);
        if (!f.exists()) {
            try {
                f.createNewFile();
            } catch (IOException e) {
                plugin.getLogger().severe("❌ Konnte Spielerdatei nicht erstellen: " + f.getAbsolutePath());
                e.printStackTrace();
            }
        }
        return YamlConfiguration.loadConfiguration(f);
    }

    private void savePlayerConfig(UUID uuid, FileConfiguration cfg) {
        try {
            cfg.save(getPlayerFile(uuid));
        } catch (IOException e) {
            plugin.getLogger().severe("❌ Konnte Spielerdatei nicht speichern: " + uuid);
            e.printStackTrace();
        }
    }

    // =============================
    //  Baum-System
    // =============================

    public boolean getBaumEnabled(UUID uuid) {
        return getPlayerConfig(uuid).getBoolean("baum.enabled", false);
    }

    public void setBaumEnabled(UUID uuid, boolean v) {
        var c = getPlayerConfig(uuid);
        c.set("baum.enabled", v);
        savePlayerConfig(uuid, c);
    }

    public double getBaumDurabilityFactor(UUID uuid) {
        return getPlayerConfig(uuid).getDouble("baum.durability-factor", 1.0D);
    }

    public void setBaumDurabilityFactor(UUID uuid, double f) {
        var c = getPlayerConfig(uuid);
        c.set("baum.durability-factor", f);
        savePlayerConfig(uuid, c);
    }

    public boolean getBaumRequireSneak(UUID uuid) {
        return getPlayerConfig(uuid).getBoolean("baum.require-sneak", true);
    }

    public void setBaumRequireSneak(UUID uuid, boolean v) {
        var c = getPlayerConfig(uuid);
        c.set("baum.require-sneak", v);
        savePlayerConfig(uuid, c);
    }

    public boolean getBaumAutoReplant(UUID uuid) {
        return getPlayerConfig(uuid).getBoolean("baum.auto-replant", false);
    }

    public void setBaumAutoReplant(UUID uuid, boolean v) {
        var c = getPlayerConfig(uuid);
        c.set("baum.auto-replant", v);
        savePlayerConfig(uuid, c);
    }

    // =============================
    // ⛏ Erz-System
    // =============================

    public boolean getErzEnabled(UUID uuid) {
        return getPlayerConfig(uuid).getBoolean("erz.enabled", false);
    }

    public void setErzEnabled(UUID uuid, boolean v) {
        var c = getPlayerConfig(uuid);
        c.set("erz.enabled", v);
        savePlayerConfig(uuid, c);
    }

    public double getErzDurabilityFactor(UUID uuid) {
        return getPlayerConfig(uuid).getDouble("erz.durability-factor", 1.0D);
    }

    public void setErzDurabilityFactor(UUID uuid, double f) {
        var c = getPlayerConfig(uuid);
        c.set("erz.durability-factor", f);
        savePlayerConfig(uuid, c);
    }

    public boolean getErzRequireSneak(UUID uuid) {
        return getPlayerConfig(uuid).getBoolean("erz.require-sneak", true);
    }

    public void setErzRequireSneak(UUID uuid, boolean v) {
        var c = getPlayerConfig(uuid);
        c.set("erz.require-sneak", v);
        savePlayerConfig(uuid, c);
    }

    public boolean getErzOnlyStandard(UUID uuid) {
        return getPlayerConfig(uuid).getBoolean("erz.only-standard", true);
    }

    public void setErzOnlyStandard(UUID uuid, boolean v) {
        var c = getPlayerConfig(uuid);
        c.set("erz.only-standard", v);
        savePlayerConfig(uuid, c);
    }

    // =============================
    //  Stats-System
    // =============================

    public void addOreType(UUID uuid, Material mat, int amount) {
        if (mat == null) return;
        Material normalized = normalizeOreType(mat);
        var cfg = getPlayerConfig(uuid);
        String path = "stats.ores." + normalized.name();
        int current = cfg.getInt(path, 0);
        cfg.set(path, current + amount);
        savePlayerConfig(uuid, cfg);
    }

    public Map<String, Integer> getOreStats(UUID uuid) {
        var cfg = getPlayerConfig(uuid);
        Map<String, Integer> map = new HashMap<>();
        if (cfg.isConfigurationSection("stats.ores")) {
            for (String key : cfg.getConfigurationSection("stats.ores").getKeys(false)) {
                map.put(key, cfg.getInt("stats.ores." + key));
            }
        }
        return map;
    }

    public void addTreeType(UUID uuid, Material mat, int amount) {
        if (mat == null) return;
        var cfg = getPlayerConfig(uuid);
        String path = "stats.trees." + mat.name();
        int current = cfg.getInt(path, 0);
        cfg.set(path, current + amount);
        savePlayerConfig(uuid, cfg);
    }

    public Map<String, Integer> getTreeStats(UUID uuid) {
        var cfg = getPlayerConfig(uuid);
        Map<String, Integer> map = new HashMap<>();
        if (cfg.isConfigurationSection("stats.trees")) {
            for (String key : cfg.getConfigurationSection("stats.trees").getKeys(false)) {
                map.put(key, cfg.getInt("stats.trees." + key));
            }
        }
        return map;
    }

    // =============================
    // ⚙️ Spieler-Einstellungen
    // =============================

    public boolean isFlyEnabled(UUID uuid) {
        return getPlayerConfig(uuid).getBoolean("player.fly", false);
    }

    public void setFlyEnabled(UUID uuid, boolean v) {
        var c = getPlayerConfig(uuid);
        c.set("player.fly", v);
        savePlayerConfig(uuid, c);
    }

    public void setMagnetEnabled(UUID uuid, boolean v) {
        var c = getPlayerConfig(uuid);
        c.set("player.magnet", v);
        savePlayerConfig(uuid, c);
    }

    public boolean isFastFurnaceEnabled(UUID uuid) {
        return getPlayerConfig(uuid).getBoolean("player.fast_furnace", false);
    }

    public void setFastFurnaceEnabled(UUID uuid, boolean v) {
        var c = getPlayerConfig(uuid);
        c.set("player.fast_furnace", v);
        savePlayerConfig(uuid, c);
    }

    // =============================
    // 💤 AFK-SYSTEM
    // =============================

    public boolean toggleAfk(UUID uuid) {
        boolean isAfk = afkStatus.getOrDefault(uuid, false);
        afkStatus.put(uuid, !isAfk);
        return !isAfk;
    }

    public boolean isAfk(UUID uuid) {
        return afkStatus.getOrDefault(uuid, false);
    }

    public void setAfk(UUID uuid, boolean value) {
        afkStatus.put(uuid, value);
    }

    // =============================
    // 🎁 Auto-Pickup / Magnet
    // =============================

    public boolean isAutoPickupEnabled(UUID uuid) {
        return getPlayerConfig(uuid).getBoolean("player.auto_pickup", true);
    }

    public void setAutoPickupEnabled(UUID uuid, boolean value) {
        var cfg = getPlayerConfig(uuid);
        cfg.set("player.auto_pickup", value);
        savePlayerConfig(uuid, cfg);
    }

    public boolean isMagnetEnabled(UUID uuid) {
        return getPlayerConfig(uuid).getBoolean("player.magnet", true);
    }

    // =============================
    // ⚙️ Server Settings (global)
    // =============================

    public boolean isFarmProtectEnabled() {
        return plugin.getConfig().getBoolean("server.farm_protect", true);
    }

    public void setFarmProtectEnabled(boolean v) {
        plugin.getConfig().set("server.farm_protect", v);
        plugin.saveConfig();
    }

    public boolean isAntiCreeperEnabled() {
        return plugin.getConfig().getBoolean("server.anti_creeper", true);
    }

    public void setAntiCreeperEnabled(boolean v) {
        plugin.getConfig().set("server.anti_creeper", v);
        plugin.saveConfig();
    }

    public boolean isTntBlockDamageEnabled() {
        return plugin.getConfig().getBoolean("server.tnt_block_damage", true);
    }

    public void setTntBlockDamageEnabled(boolean v) {
        plugin.getConfig().set("server.tnt_block_damage", v);
        plugin.saveConfig();
    }

    public boolean isPvpEnabled() {
        return plugin.getConfig().getBoolean("server.pvp", true);
    }

    public void setPvpEnabled(boolean v) {
        plugin.getConfig().set("server.pvp", v);
        plugin.saveConfig();
    }

    public boolean isMobGriefingEnabled() {
        return plugin.getConfig().getBoolean("server.mob_griefing", true);
    }

    public void setMobGriefingEnabled(boolean v) {
        plugin.getConfig().set("server.mob_griefing", v);
        plugin.saveConfig();
    }

    // =============================
    // 🔐 Rollen & Berechtigungen
    // =============================

    public Set<String> getPermissions(UUID uuid) {
        if (!plugin.getConfig().contains("permissions." + uuid)) {
            return new HashSet<>();
        }
        return new HashSet<>(plugin.getConfig().getStringList("permissions." + uuid));
    }

    public void setPermissions(UUID uuid, Set<String> perms) {
        plugin.getConfig().set("permissions." + uuid, new ArrayList<>(perms));
        plugin.saveConfig();
    }

    public String getRole(UUID uuid) {
        return plugin.getConfig().getString("roles." + uuid, "spieler");
    }

    public void setRole(UUID uuid, String role) {
        plugin.getConfig().set("roles." + uuid, role.toLowerCase());
        plugin.saveConfig();
    }

    // ==================== HOME SYSTEM CONFIG ==================== //

    // Partikel an/aus pro Spieler
    public boolean isHomeParticlesEnabled(UUID uuid) {
        FileConfiguration cfg = plugin.getConfig();
        boolean def = cfg.getBoolean("home.default.particles", true);
        return cfg.getBoolean("players." + uuid + ".home.particles", def);
    }

    public void setHomeParticlesEnabled(UUID uuid, boolean enabled) {
        FileConfiguration cfg = plugin.getConfig();
        cfg.set("players." + uuid + ".home.particles", enabled);
        plugin.saveConfig();
    }

    // Delay bis zum Teleport
    public int getHomeTeleportDelay(UUID uuid) {
        FileConfiguration cfg = plugin.getConfig();
        int def = cfg.getInt("home.default.delay", 3);
        return cfg.getInt("players." + uuid + ".home.delay", def);
    }

    public void setHomeTeleportDelay(UUID uuid, int delay) {
        FileConfiguration cfg = plugin.getConfig();
        cfg.set("players." + uuid + ".home.delay", delay);
        plugin.saveConfig();
    }

    // Max. Homes pro Spieler (Default 10)
    public int getMaxHomes(UUID uuid) {
        FileConfiguration cfg = plugin.getConfig();
        int def = cfg.getInt("home.default.maxhomes", 10);
        return cfg.getInt("players." + uuid + ".home.maxhomes", def);
    }

    public void setMaxHomes(UUID uuid, int amount) {
        FileConfiguration cfg = plugin.getConfig();
        cfg.set("players." + uuid + ".home.maxhomes", amount);
        plugin.saveConfig();
    }

    // Cooldown pro Spieler
    public int getHomeCooldown(UUID uuid) {
        FileConfiguration cfg = plugin.getConfig();
        int def = cfg.getInt("home.default.cooldown", 0);
        return cfg.getInt("players." + uuid + ".home.cooldown", def);
    }

    public void setHomeCooldown(UUID uuid, int seconds) {
        FileConfiguration cfg = plugin.getConfig();
        cfg.set("players." + uuid + ".home.cooldown", seconds);
        plugin.saveConfig();
    }

    // Instant-TP pro Spieler
    public boolean isHomeInstantTeleport(UUID uuid) {
        FileConfiguration cfg = plugin.getConfig();
        boolean def = cfg.getBoolean("home.default.instant", false);
        return cfg.getBoolean("players." + uuid + ".home.instant", def);
    }

    public void setHomeInstantTeleport(UUID uuid, boolean instant) {
        FileConfiguration cfg = plugin.getConfig();
        cfg.set("players." + uuid + ".home.instant", instant);
        plugin.saveConfig();
    }

    // Move-Cancel pro Spieler
    public boolean isHomeMoveCancelEnabled(UUID uuid) {
        FileConfiguration cfg = plugin.getConfig();
        boolean def = cfg.getBoolean("home.default.movecancel", true);
        return cfg.getBoolean("players." + uuid + ".home.movecancel", def);
    }

    public void setHomeMoveCancelEnabled(UUID uuid, boolean enabled) {
        FileConfiguration cfg = plugin.getConfig();
        cfg.set("players." + uuid + ".home.movecancel", enabled);
        plugin.saveConfig();
    }



        // Hologramme pro Spieler gespeichert
public boolean isHomeHologramEnabled(UUID uuid) {
    FileConfiguration cfg = plugin.getConfig();
    boolean def = true;
    return cfg.getBoolean("players." + uuid + ".home.holograms", def);
}

public void setHomeHologramEnabled(UUID uuid, boolean enabled) {
    FileConfiguration cfg = plugin.getConfig();
    cfg.set("players." + uuid + ".home.holograms", enabled);
    plugin.saveConfig();
}



    // =============================
    // Util
    // =============================

    private Material normalizeOreType(Material mat) {
        if (mat.name().startsWith("DEEPSLATE_")) {
            try {
                return Material.valueOf(mat.name().replace("DEEPSLATE_", ""));
            } catch (IllegalArgumentException ignored) {}
        }
        return mat;
    }
}
