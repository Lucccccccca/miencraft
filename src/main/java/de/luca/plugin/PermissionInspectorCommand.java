package de.luca.plugin;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class PermissionInspectorCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender s, Command cmd, String label, String[] args) {

        if (!(s instanceof Player p)) {
            s.sendMessage("Nur Spieler!");
            return true;
        }

        if (!p.hasPermission("lucacrafter.permcheck.use")) {
            p.sendMessage(ChatColor.RED + "Keine Rechte!");
            return true;
        }

        if (args.length != 1) {
            p.sendMessage(ChatColor.RED + "Nutze: /permcheck <Spieler>");
            return true;
        }

        Player target = Bukkit.getPlayer(args[0]);
        if (target == null) {
            p.sendMessage(ChatColor.RED + "Spieler nicht gefunden!");
            return true;
        }

        PermissionInspectorGUI.open(p, target);
        return true;
    }
}
