package de.luca.plugin;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Verwaltet Custom-Prefixe pro Spieler (Text, Farbe, aktiviert?)
 * Daten liegen in plugins/LucaCrafterPlugin/prefixes.yml
 */
public class PlayerPrefixManager {

    private final LucaCrafterPlugin plugin;

    private final Map<UUID, CustomPrefixData> cache = new HashMap<>();

    private File file;
    private FileConfiguration cfg;

    public PlayerPrefixManager(LucaCrafterPlugin plugin) {
        this.plugin = plugin;
        this.file = new File(plugin.getDataFolder(), "prefixes.yml");

        if (!file.exists()) {
            try {
                file.getParentFile().mkdirs();
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        load();
    }

    // --------------------------------------------------------
    // Laden / Speichern
    // --------------------------------------------------------

    public void load() {
        cfg = YamlConfiguration.loadConfiguration(file);
        cache.clear();

        if (cfg.isConfigurationSection("players")) {
            for (String uuidStr : cfg.getConfigurationSection("players").getKeys(false)) {
                try {
                    UUID uuid = UUID.fromString(uuidStr);
                    boolean enabled = cfg.getBoolean("players." + uuidStr + ".enabled", false);
                    String text = cfg.getString("players." + uuidStr + ".text", "");
                    String color = cfg.getString("players." + uuidStr + ".color", "&f");
                    cache.put(uuid, new CustomPrefixData(enabled, text, color));
                } catch (IllegalArgumentException ex) {
                    // kaputte UUID ignorieren
                }
            }
        }
    }

    public void save() {
        if (cfg == null) return;

        cfg.set("players", null); // alles neu schreiben

        for (Map.Entry<UUID, CustomPrefixData> entry : cache.entrySet()) {
            UUID uuid = entry.getKey();
            CustomPrefixData data = entry.getValue();
            String base = "players." + uuid.toString();
            cfg.set(base + ".enabled", data.enabled);
            cfg.set(base + ".text", data.text);
            cfg.set(base + ".color", data.colorCode);
        }

        try {
            cfg.save(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // --------------------------------------------------------
    // Getter / Setter
    // --------------------------------------------------------

    private CustomPrefixData getData(UUID uuid) {
        return cache.computeIfAbsent(uuid, id -> new CustomPrefixData(false, "", "&f"));
    }

    public boolean isCustomEnabled(UUID uuid) {
        return getData(uuid).enabled;
    }

    public void setCustomEnabled(UUID uuid, boolean enabled) {
        getData(uuid).enabled = enabled;
        save();
    }

    /**
     * Nur der reine Text ohne Farbe.
     */
    public String getCustomText(UUID uuid) {
        return getData(uuid).text;
    }

    public void setCustomText(UUID uuid, String text) {
        if (text == null) text = "";
        // kleine Sicherheitsbegrenzung
        if (text.length() > 16) {
            text = text.substring(0, 16);
        }
        getData(uuid).text = text;
        save();
    }

    /**
     * Farbcode im &-Format, z.B. "&a", "&c" usw.
     */
    public String getColorCode(UUID uuid) {
        return getData(uuid).colorCode;
    }

    public void setColorCode(UUID uuid, String colorCode) {
        if (colorCode == null || colorCode.isEmpty()) {
            colorCode = "&f";
        }
        getData(uuid).colorCode = colorCode;
        save();
    }

    /**
     * Gibt den fertigen Custom-Prefix im &-Format zurück, z.B. "&aKing ".
     * Wenn kein Text gesetzt ist → "".
     */
    public String getFormattedCustomPrefix(UUID uuid) {
        CustomPrefixData data = getData(uuid);
        if (data.text == null || data.text.trim().isEmpty()) {
            return "";
        }
        return data.colorCode + data.text + " ";
    }

    // kleine Datenklasse
    private static class CustomPrefixData {
        boolean enabled;
        String text;
        String colorCode;

        CustomPrefixData(boolean enabled, String text, String colorCode) {
            this.enabled = enabled;
            this.text = text;
            this.colorCode = colorCode;
        }
    }
}
