package de.luca.plugin;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.*;
import org.bukkit.event.player.PlayerJoinEvent;

import java.util.Set;
import java.util.UUID;

public class FreundeJoinListener implements Listener {

    private final LucaCrafterPlugin plugin;

    public FreundeJoinListener(LucaCrafterPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        Player p = e.getPlayer();
        UUID me = p.getUniqueId();

        // Freunde online -> IMMER anzeigen (türkis + gelb)
        Set<UUID> friends = plugin.getFriendManager().getFriends(me);
        long online = friends.stream().filter(u -> {
            Player op = Bukkit.getPlayer(u);
            return op != null && op.isOnline();
        }).count();
        p.sendMessage("§bEs sind aktuell §e" + online + " §bFreunde online.");

        // Freundschaftsanfragen -> nur wenn > 0
        int requests = plugin.getFriendManager().getRequestsReceived(me).size();
        if (requests > 0) {
            p.sendMessage("§aDu hast §e" + requests + " §aoffene Freundschaftsanfrage" + (requests == 1 ? "!" : "n!"));
            p.sendMessage("§7Nutze §e/freunde anfragen§7, um sie anzusehen.");
        }
    }
}
