package de.luca.plugin;

import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;


import org.bukkit.GameMode;
import java.util.HashMap;
import java.util.UUID;

public class AfkCommand implements CommandExecutor, Listener {

    private final LucaCrafterPlugin plugin;
    private final HashMap<UUID, GameMode> previousModes = new HashMap<>();

    public AfkCommand(LucaCrafterPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player player)) return true;

        boolean isAfk = plugin.getConfigManager().toggleAfk(player.getUniqueId());

        if (isAfk) {
            // 👇 alten Modus speichern
            previousModes.put(player.getUniqueId(), player.getGameMode());

            player.setGameMode(GameMode.SPECTATOR);
            player.sendMessage(ChatColor.GRAY + "💤 Du bist jetzt AFK (Spectator).");
            player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_AMBIENT, 1f, 0.8f);
        } else {
            // 👇 alten Modus wiederherstellen
            GameMode old = previousModes.getOrDefault(player.getUniqueId(), GameMode.SURVIVAL);
            player.setGameMode(old);

            player.sendMessage(ChatColor.GREEN + "✅ Du bist nicht mehr AFK.");
            player.playSound(player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1f, 1.2f);
        }
        return true;
    }

    @EventHandler
    public void onMove(PlayerMoveEvent e) {
        Player p = e.getPlayer();
        if (plugin.getConfigManager().isAfk(p.getUniqueId())) {
            plugin.getConfigManager().setAfk(p.getUniqueId(), false);
            p.setGameMode(previousModes.getOrDefault(p.getUniqueId(), GameMode.SURVIVAL));
            p.sendMessage(ChatColor.YELLOW + "💡 Du bist wieder aktiv!");
        }
    }
}




