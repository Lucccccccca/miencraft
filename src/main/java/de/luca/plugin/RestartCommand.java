package de.luca.plugin;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

/**
 * Führt einen Server-Neustart mit Countdown durch.
 * Beispiel: /restart 30 startet einen 30-Sekunden-Countdown,
 * danach wird der Server gestoppt (Restart hängt vom Startscript ab).
 */
public class RestartCommand implements CommandExecutor {
    private final LucaCrafterPlugin plugin;

    public RestartCommand(LucaCrafterPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        // Berechtigung prüfen (auch Console darf)
        if (sender instanceof Player p) {
            if (!p.hasPermission("lucacrafter.server.restart") && !p.isOp()) {
                p.sendMessage(ChatColor.RED + "Du hast keine Berechtigung, den Server neu zu starten.");
                return true;
            }
        }

        int seconds = 10; // Standard: 10 Sekunden
        if (args.length > 0) {
            try {
                seconds = Integer.parseInt(args[0]);
                if (seconds <= 0) seconds = 1;
            } catch (NumberFormatException e) {
                sender.sendMessage(ChatColor.RED + "Ungültige Zahl, verwende z. B. /restart 30");
                return true;
            }
        }

        // Startnachricht
        Bukkit.broadcastMessage(ChatColor.RED + "⚠️ Server-Neustart in " + seconds + " Sekunden...");

        // Countdown
        final int[] counter = { seconds };
        new BukkitRunnable() {
            @Override
            public void run() {
                counter[0]--;
                if (counter[0] <= 0) {
                    Bukkit.broadcastMessage(ChatColor.RED + "🔄 Server wird jetzt neugestartet!");
                    Bukkit.getServer().shutdown(); // beende Server (Restart je nach Startscript)
                    cancel();
                } else {
                    Bukkit.broadcastMessage(ChatColor.RED + "⚠️ Server-Neustart in " + counter[0] + " Sekunden...");
                }
            }
        }.runTaskTimer(plugin, 20L, 20L); // 1 Sekunde (20 Ticks) Intervall

        return true;
    }
}
