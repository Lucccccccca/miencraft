package de.luca.plugin;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Fängt den Chat ab, wenn ein Spieler gerade seinen Prefix-Text eingibt.
 */
public class PrefixChatListener implements Listener {

    private final LucaCrafterPlugin plugin;
    private final PlayerPrefixManager prefixManager;
    private final PrefixUpdater prefixUpdater;

    private final Set<UUID> waitingForPrefix = ConcurrentHashMap.newKeySet();

    public PrefixChatListener(LucaCrafterPlugin plugin) {
        this.plugin = plugin;
        this.prefixManager = plugin.getPlayerPrefixManager();
        this.prefixUpdater = plugin.getPrefixUpdater();
    }

    public void startPrefixInput(Player player) {
        waitingForPrefix.add(player.getUniqueId());
        player.sendMessage("§8[§aPrefix§8] §7Gib jetzt deinen neuen Prefix im Chat ein.");
        player.sendMessage("§8[§aPrefix§8] §7Schreibe §c'abbrechen'§7, um abzubrechen.");
    }

    @EventHandler
    public void onChat(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();
        UUID uuid = player.getUniqueId();

        if (!waitingForPrefix.contains(uuid)) return;

        event.setCancelled(true);
        String msg = event.getMessage().trim();

        if (msg.equalsIgnoreCase("abbrechen")) {
            waitingForPrefix.remove(uuid);
            player.sendMessage("§8[§aPrefix§8] §7Eingabe abgebrochen.");
            // zurück ins GUI
            Bukkit.getScheduler().runTask(plugin, () ->
                    PrefixGUI.open(plugin, player));
            return;
        }

        // Farbcodes im Text verhindern – Farbe wird separat gewählt
        msg = ChatColor.stripColor(ChatColor.translateAlternateColorCodes('&', msg));

        if (msg.length() > 16) {
            msg = msg.substring(0, 16);
            player.sendMessage("§8[§aPrefix§8] §7Prefix wurde auf 16 Zeichen gekürzt.");
        }

        prefixManager.setCustomText(uuid, msg);
        prefixManager.setCustomEnabled(uuid, true);
        waitingForPrefix.remove(uuid);

        player.sendMessage("§8[§aPrefix§8] §7Dein Prefix lautet jetzt: §f" + msg);

        Bukkit.getScheduler().runTask(plugin, () -> {
            prefixUpdater.update(player);
            PrefixGUI.open(plugin, player);
        });
    }
}
