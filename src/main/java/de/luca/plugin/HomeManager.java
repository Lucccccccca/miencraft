package de.luca.plugin;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class HomeManager {

    private final LucaCrafterPlugin plugin;

    public HomeManager(LucaCrafterPlugin plugin) {
        this.plugin = plugin;
    }

    private File getPlayerFile(UUID uuid) {
        File folder = new File(plugin.getDataFolder(), "homes");
        if (!folder.exists()) folder.mkdirs();
        return new File(folder, uuid.toString() + ".yml");
    }

    public Map<String, Home> getHomes(UUID uuid) {
        File f = getPlayerFile(uuid);
        YamlConfiguration cfg = YamlConfiguration.loadConfiguration(f);
        Map<String, Home> homes = new HashMap<>();

        if (cfg.isConfigurationSection("homes")) {
            for (String key : cfg.getConfigurationSection("homes").getKeys(false)) {
                String path = "homes." + key + ".";
                String world = cfg.getString(path + "world");
                double x = cfg.getDouble(path + "x");
                double y = cfg.getDouble(path + "y");
                double z = cfg.getDouble(path + "z");
                float yaw = (float) cfg.getDouble(path + "yaw");
                float pitch = (float) cfg.getDouble(path + "pitch");

                Location loc = new Location(Bukkit.getWorld(world), x, y, z, yaw, pitch);
                homes.put(key, new Home(key, loc));
            }
        }

        return homes;
    }

    public boolean hasHome(UUID uuid, String name) {
        return getHomes(uuid).containsKey(name.toLowerCase());
    }

    public void setHome(UUID uuid, String name, Location loc) {
        File f = getPlayerFile(uuid);
        YamlConfiguration cfg = YamlConfiguration.loadConfiguration(f);

        String path = "homes." + name.toLowerCase() + ".";
        cfg.set(path + "world", loc.getWorld().getName());
        cfg.set(path + "x", loc.getX());
        cfg.set(path + "y", loc.getY());
        cfg.set(path + "z", loc.getZ());
        cfg.set(path + "yaw", loc.getYaw());
        cfg.set(path + "pitch", loc.getPitch());

        try {
            cfg.save(f);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void deleteHome(UUID uuid, String name) {
        File f = getPlayerFile(uuid);
        YamlConfiguration cfg = YamlConfiguration.loadConfiguration(f);
        cfg.set("homes." + name.toLowerCase(), null);

        try {
            cfg.save(f);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
