package de.luca.plugin;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

import java.util.*;

public class HomeTeleportHandler implements Listener {

    private final LucaCrafterPlugin plugin;
    private final Map<UUID, Boolean> cancelMove = new HashMap<>();

    public HomeTeleportHandler(LucaCrafterPlugin plugin) {
        this.plugin = plugin;
    }

    public boolean isMoveCancelled(UUID uuid) {
        return cancelMove.getOrDefault(uuid, false);
    }

    public void setMoveCancel(UUID uuid, boolean state) {
        cancelMove.put(uuid, state);
    }

    @EventHandler
    public void onMove(PlayerMoveEvent e) {
        Player p = e.getPlayer();

        if (!isMoveCancelled(p.getUniqueId())) return;

        if (e.getFrom().distanceSquared(e.getTo()) > 0.1) {
            cancelMove.put(p.getUniqueId(), false);
            p.sendMessage("§cTeleport abgebrochen! Du hast dich bewegt.");
        }
    }
}
