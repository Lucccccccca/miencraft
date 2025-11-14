package de.luca.plugin;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class HomeHologramManager {

    private final LucaCrafterPlugin plugin;

    // Jeder Spieler: <HomeName, ArmorStand>
    private final Map<UUID, Map<String, ArmorStand>> holograms = new HashMap<>();

    public HomeHologramManager(LucaCrafterPlugin plugin) {
        this.plugin = plugin;
    }

    public void createHologram(Player p, Home home) {

        UUID uuid = p.getUniqueId();

        // Spieler hat Hologramme deaktiviert → nicht erstellen
        if (!plugin.getConfigManager().isHomeHologramEnabled(uuid)) return;

        holograms.putIfAbsent(uuid, new HashMap<>());

        // Hologramm existiert schon → löschen & neu setzen
        removeHologram(p, home.getName());

        Location loc = home.getLocation().clone().add(0.5, 1.7, 0.5);

        ArmorStand as = home.getLocation().getWorld().spawn(loc, ArmorStand.class, stand -> {
            stand.setCustomName("§bHome: §f" + home.getName());
            stand.setCustomNameVisible(true);
            stand.setInvisible(true);
            stand.setMarker(true);
            stand.setGravity(false);
            stand.setSmall(true);
        });

        holograms.get(uuid).put(home.getName().toLowerCase(), as);
    }

    public void removeHologram(Player p, String homeName) {
        UUID uuid = p.getUniqueId();

        if (!holograms.containsKey(uuid)) return;

        ArmorStand as = holograms.get(uuid).get(homeName.toLowerCase());
        if (as != null) {
            as.remove();
            holograms.get(uuid).remove(homeName.toLowerCase());
        }
    }

    public void removeAll(Player p) {
        UUID uuid = p.getUniqueId();
        if (!holograms.containsKey(uuid)) return;

        holograms.get(uuid).values().forEach(ArmorStand::remove);
        holograms.remove(uuid);
    }

    public void refreshPlayer(Player p) {
        removeAll(p);

        if (!plugin.getConfigManager().isHomeHologramEnabled(p.getUniqueId())) return;

        plugin.getHomeManager().getHomes(p.getUniqueId()).values().forEach(home -> {
            createHologram(p, home);
        });
    }
}
