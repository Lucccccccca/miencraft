package de.luca.plugin;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.concurrent.atomic.AtomicInteger;

public class RestartServerCommand implements CommandExecutor {

    private final LucaCrafterPlugin plugin;
    private boolean isRestarting = false;

    public RestartServerCommand(LucaCrafterPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("lucacrafter.server.restart")) {
            sender.sendMessage(ChatColor.RED + "❌ Du hast keine Berechtigung für diesen Befehl!");
            return true;
        }

        if (isRestarting) {
            sender.sendMessage(ChatColor.YELLOW + "⚠️ Ein Neustart läuft bereits!");
            return true;
        }

        int seconds = 10;
        if (args.length > 0) {
            try {
                seconds = Integer.parseInt(args[0]);
            } catch (NumberFormatException e) {
                sender.sendMessage(ChatColor.RED + "Bitte gib eine gültige Zahl in Sekunden an!");
                return true;
            }
        }

        isRestarting = true;
        AtomicInteger countdown = new AtomicInteger(seconds);
        Bukkit.broadcastMessage(ChatColor.GOLD + "🔁 Server-Neustart in " + ChatColor.YELLOW + seconds + " Sekunden...");

        Bukkit.getScheduler().runTaskTimer(plugin, task -> {
            int current = countdown.getAndDecrement();
            if (current <= 0) {
                Bukkit.broadcastMessage(ChatColor.RED + "🔄 Der Server wird jetzt neu gestartet!");
                for (Player p : Bukkit.getOnlinePlayers()) {
                    p.playSound(p.getLocation(), Sound.ENTITY_ENDER_DRAGON_GROWL, 1f, 1f);
                }
                task.cancel();
                Bukkit.shutdown();
                return;
            }
            if (current <= 10 || current % 5 == 0) {
                Bukkit.broadcastMessage(ChatColor.YELLOW + "⚠️ Server-Neustart in " + ChatColor.GOLD + current + ChatColor.YELLOW + " Sekunden...");
                for (Player p : Bukkit.getOnlinePlayers()) {
                    p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_BELL, 1f, 1f);
                }
            }
        }, 0L, 20L);

        return true;
    }
}
