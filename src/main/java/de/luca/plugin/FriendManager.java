package de.luca.plugin;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class FriendManager {

    private final LucaCrafterPlugin plugin;
    private final File file;
    private final FileConfiguration config;

    private final Map<UUID, Set<UUID>> friends = new HashMap<>();
    private final Map<UUID, Set<UUID>> blocked = new HashMap<>();
    private final Map<UUID, Set<UUID>> favorites = new HashMap<>();
    private final Map<UUID, Set<UUID>> requestsReceived = new HashMap<>();
    private final Map<UUID, Set<UUID>> requestsSent = new HashMap<>();

    public FriendManager(LucaCrafterPlugin plugin) {
        this.plugin = plugin;
        this.file = new File(plugin.getDataFolder(), "friends.yml");
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                plugin.getLogger().severe("Konnte friends.yml nicht erstellen!");
                e.printStackTrace();
            }
        }
        this.config = YamlConfiguration.loadConfiguration(file);
        load();
    }

    private void load() {
        if (!config.isConfigurationSection("friends")) return;

        for (String uuidStr : config.getConfigurationSection("friends").getKeys(false)) {
            UUID uuid;
            try {
                uuid = UUID.fromString(uuidStr);
            } catch (IllegalArgumentException e) {
                continue;
            }

            friends.put(uuid, loadSet(uuidStr, "friends"));
            blocked.put(uuid, loadSet(uuidStr, "blocked"));
            favorites.put(uuid, loadSet(uuidStr, "favorites"));
            requestsReceived.put(uuid, loadSet(uuidStr, "requests-received"));
            requestsSent.put(uuid, loadSet(uuidStr, "requests-sent"));
        }
    }

    private Set<UUID> loadSet(String uuidStr, String key) {
        List<String> list = config.getStringList("friends." + uuidStr + "." + key);
        Set<UUID> set = new HashSet<>();
        for (String s : list) {
            try {
                set.add(UUID.fromString(s));
            } catch (IllegalArgumentException ignored) {}
        }
        return set;
    }

    private void save() {
        config.set("friends", null);

        Set<UUID> all = new HashSet<>();
        all.addAll(friends.keySet());
        all.addAll(blocked.keySet());
        all.addAll(favorites.keySet());
        all.addAll(requestsReceived.keySet());
        all.addAll(requestsSent.keySet());

        for (UUID uuid : all) {
            String base = "friends." + uuid.toString() + ".";
            config.set(base + "friends", toStringList(friends.get(uuid)));
            config.set(base + "blocked", toStringList(blocked.get(uuid)));
            config.set(base + "favorites", toStringList(favorites.get(uuid)));
            config.set(base + "requests-received", toStringList(requestsReceived.get(uuid)));
            config.set(base + "requests-sent", toStringList(requestsSent.get(uuid)));
        }

        try {
            config.save(file);
        } catch (IOException e) {
            plugin.getLogger().severe("Konnte friends.yml nicht speichern!");
            e.printStackTrace();
        }
    }

    private List<String> toStringList(Set<UUID> set) {
        List<String> list = new ArrayList<>();
        if (set == null) return list;
        for (UUID u : set) list.add(u.toString());
        return list;
    }

    private Set<UUID> getOrCreate(Map<UUID, Set<UUID>> map, UUID uuid) {
        return map.computeIfAbsent(uuid, k -> new HashSet<>());
    }

    // ====== GETTER ======

    public Set<UUID> getFriends(UUID uuid) {
        return new HashSet<>(friends.getOrDefault(uuid, Collections.emptySet()));
    }

    public Set<UUID> getBlocked(UUID uuid) {
        return new HashSet<>(blocked.getOrDefault(uuid, Collections.emptySet()));
    }

    public Set<UUID> getFavorites(UUID uuid) {
        return new HashSet<>(favorites.getOrDefault(uuid, Collections.emptySet()));
    }

    public Set<UUID> getRequestsReceived(UUID uuid) {
        return new HashSet<>(requestsReceived.getOrDefault(uuid, Collections.emptySet()));
    }

    public Set<UUID> getRequestsSent(UUID uuid) {
        return new HashSet<>(requestsSent.getOrDefault(uuid, Collections.emptySet()));
    }

    public boolean areFriends(UUID a, UUID b) {
        return friends.getOrDefault(a, Collections.emptySet()).contains(b)
                && friends.getOrDefault(b, Collections.emptySet()).contains(a);
    }

    public boolean isBlocked(UUID owner, UUID target) {
        return blocked.getOrDefault(owner, Collections.emptySet()).contains(target);
    }

    public boolean isFavorite(UUID owner, UUID target) {
        return favorites.getOrDefault(owner, Collections.emptySet()).contains(target);
    }

    // ====== FRIEND REQUESTS ======

    public boolean sendFriendRequest(UUID from, UUID to) {
        if (from.equals(to)) return false;
        if (isBlocked(from, to) || isBlocked(to, from)) return false;
        if (areFriends(from, to)) return false;

        Set<UUID> rec = getOrCreate(requestsReceived, to);
        Set<UUID> sent = getOrCreate(requestsSent, from);

        if (rec.contains(from)) return false; // bereits offen

        rec.add(from);
        sent.add(to);

        save();
        return true;
    }

    public boolean acceptRequest(UUID target, UUID from) {
        Set<UUID> rec = getOrCreate(requestsReceived, target);
        Set<UUID> sent = getOrCreate(requestsSent, from);

        if (!rec.contains(from)) return false;

        rec.remove(from);
        sent.remove(target);

        getOrCreate(friends, target).add(from);
        getOrCreate(friends, from).add(target);

        save();
        return true;
    }

    public boolean denyRequest(UUID target, UUID from) {
        Set<UUID> rec = getOrCreate(requestsReceived, target);
        Set<UUID> sent = getOrCreate(requestsSent, from);

        if (!rec.contains(from)) return false;

        rec.remove(from);
        sent.remove(target);

        save();
        return true;
    }

    // ====== FRIEND REMOVE / BLOCK / FAVORITE ======

    public boolean removeFriend(UUID a, UUID b) {
        boolean changed = false;
        if (friends.containsKey(a) && friends.get(a).remove(b)) changed = true;
        if (friends.containsKey(b) && friends.get(b).remove(a)) changed = true;

        if (favorites.containsKey(a)) favorites.get(a).remove(b);
        if (favorites.containsKey(b)) favorites.get(b).remove(a);

        if (changed) save();
        return changed;
    }

    public boolean block(UUID owner, UUID target) {
        if (owner.equals(target)) return false;

        getOrCreate(blocked, owner).add(target);

        // Freundschaft & Requests entfernen
        removeFriend(owner, target);
        getOrCreate(requestsReceived, owner).remove(target);
        getOrCreate(requestsReceived, target).remove(owner);
        getOrCreate(requestsSent, owner).remove(target);
        getOrCreate(requestsSent, target).remove(owner);

        save();
        return true;
    }

    public boolean unblock(UUID owner, UUID target) {
        if (!blocked.containsKey(owner)) return false;
        boolean changed = blocked.get(owner).remove(target);
        if (changed) save();
        return changed;
    }

    public boolean toggleFavorite(UUID owner, UUID target) {
        if (!areFriends(owner, target)) return false;
        Set<UUID> set = getOrCreate(favorites, owner);
        boolean nowFav;
        if (set.contains(target)) {
            set.remove(target);
            nowFav = false;
        } else {
            set.add(target);
            nowFav = true;
        }
        save();
        return nowFav;
    }

    // ====== UTIL ======

    public String getName(UUID uuid) {
        OfflinePlayer op = Bukkit.getOfflinePlayer(uuid);
        return op.getName() == null ? uuid.toString() : op.getName();
    }
}
