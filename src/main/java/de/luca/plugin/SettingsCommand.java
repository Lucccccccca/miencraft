package de.luca.plugin;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SettingsCommand implements CommandExecutor {

    private final LucaCrafterPlugin plugin;

    public SettingsCommand(LucaCrafterPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player p)) {
            sender.sendMessage(ChatColor.RED + "Nur Spieler können dieses Kommando benutzen!");
            return true;
        }

        // Öffne die SPIELER-EINSTELLUNGEN (nicht Server)
        new SettingsPlayerGUI(plugin).open(p);

        p.sendMessage(ChatColor.AQUA + "🧍 Deine Spieler-Einstellungen wurden geöffnet.");
        return true;
    }
}
