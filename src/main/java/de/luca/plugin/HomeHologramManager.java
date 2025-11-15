package de.luca.plugin;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;

import java.util.*;

public class HomeHologramManager {

    private final LucaCrafterPlugin plugin;

    // Spieler -> Home-Name -> Liste von ArmorStands (mehrzeilig)
    private final Map<UUID, Map<String, List<ArmorStand>>> holograms = new HashMap<>();

    public HomeHologramManager(LucaCrafterPlugin plugin) {
        this.plugin = plugin;
    }

    public void createHologram(Player p, Home home) {
        UUID uuid = p.getUniqueId();

        if (!plugin.getConfigManager().isHomeHologramEnabled(uuid)) return;

        holograms.putIfAbsent(uuid, new HashMap<>());
        removeHologram(p, home.getName());

        Location base = home.getLocation().clone().add(0.5, 1.7, 0.5);
        String worldName = home.getLocation().getWorld() != null
                ? home.getLocation().getWorld().getName()
                : "Unbekannt";

        String line1 = ChatColor.AQUA + "✦ HOME: " + home.getName().toUpperCase() + " ✦";
        String line2 = ChatColor.GRAY + "Welt: " + ChatColor.WHITE + worldName;
        String line3 = ChatColor.GRAY + "X: " + ChatColor.WHITE + home.getLocation().getBlockX()
                + ChatColor.GRAY + " Y: " + ChatColor.WHITE + home.getLocation().getBlockY()
                + ChatColor.GRAY + " Z: " + ChatColor.WHITE + home.getLocation().getBlockZ();

        List<String> lines = Arrays.asList(line1, line2, line3);
        List<ArmorStand> stands = new ArrayList<>();

        double offset = 0.0;
        for (String line : lines) {
            Location loc = base.clone().add(0, offset, 0);

            ArmorStand as = home.getLocation().getWorld().spawn(loc, ArmorStand.class, stand -> {
                stand.setCustomName(line);
                stand.setCustomNameVisible(true);
                stand.setInvisible(true);
                stand.setMarker(true);
                stand.setGravity(false);
                stand.setSmall(true);
            });

            stands.add(as);
            offset -= 0.25;
        }

        holograms.get(uuid).put(home.getName().toLowerCase(), stands);
    }

    public void removeHologram(Player p, String homeName) {
        UUID uuid = p.getUniqueId();
        if (!holograms.containsKey(uuid)) return;

        List<ArmorStand> list = holograms.get(uuid).remove(homeName.toLowerCase());
        if (list != null) {
            list.forEach(ArmorStand::remove);
        }
    }

    public void removeAll(Player p) {
        UUID uuid = p.getUniqueId();
        if (!holograms.containsKey(uuid)) return;

        holograms.get(uuid).values().forEach(list -> list.forEach(ArmorStand::remove));
        holograms.remove(uuid);
    }

    public void refreshPlayer(Player p) {
        removeAll(p);

        if (!plugin.getConfigManager().isHomeHologramEnabled(p.getUniqueId())) return;

        plugin.getHomeManager().getHomes(p.getUniqueId()).values().forEach(home -> {
            createHologram(p, home);
        });
    }

    public void removeAllForAll() {
        for (UUID uuid : new HashSet<>(holograms.keySet())) {
            Player p = Bukkit.getPlayer(uuid);
            if (p != null && p.isOnline()) {
                removeAll(p);
            } else {
                holograms.get(uuid).values().forEach(list -> list.forEach(ArmorStand::remove));
            }
        }
        holograms.clear();
    }
}
