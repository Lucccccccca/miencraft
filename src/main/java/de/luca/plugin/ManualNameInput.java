package de.luca.plugin;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.*;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import java.util.*;

public class ManualNameInput implements Listener {

    private final LucaCrafterPlugin plugin;
    private final Set<UUID> waiting = new HashSet<>();

    public ManualNameInput(LucaCrafterPlugin plugin) {
        this.plugin = plugin;
    }

    public void start(UUID uuid) {
        waiting.add(uuid);
    }

    public boolean isWaiting(UUID uuid) {
        return waiting.contains(uuid);
    }

    public void stop(UUID uuid) {
        waiting.remove(uuid);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onChat(AsyncPlayerChatEvent e) {
        Player p = e.getPlayer();
        UUID me = p.getUniqueId();
        if (!waiting.contains(me)) return;

        e.setCancelled(true);
        String msg = e.getMessage().trim();

        if (msg.equalsIgnoreCase("abbrechen") || msg.equalsIgnoreCase("cancel")) {
            waiting.remove(me);
            p.sendMessage("§eVorgang abgebrochen.");
            return;
        }

        OfflinePlayer target = Bukkit.getOfflinePlayer(msg);
        if (target == null || (target.getName() == null && !target.hasPlayedBefore())) {
            p.sendMessage("§cSpieler nicht gefunden. Versuche es erneut oder schreibe 'abbrechen'.");
            return;
        }

        UUID tu = target.getUniqueId();
        if (plugin.getFriendManager().sendFriendRequest(me, tu)) {
            p.sendMessage("§aFreundschaftsanfrage an §e" + target.getName() + " §agesendet.");
            Player onlineTarget = target.getPlayer();
            if (onlineTarget != null && onlineTarget.isOnline()) {
                onlineTarget.sendMessage("§aDu hast eine Freundschaftsanfrage von §e" + p.getName() + "§a erhalten.");
                onlineTarget.sendMessage("§7Öffne mit §e/freunde §7das Freunde-Menü.");
            }
        } else {
            p.sendMessage("§cKonnte keine Anfrage senden (evtl. schon offen?).");
        }

        waiting.remove(me);
    }
}
