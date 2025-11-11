package de.luca.plugin;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class PermsCommand implements CommandExecutor {

    private final LucaCrafterPlugin plugin;

    public PermsCommand(LucaCrafterPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player p)) {
            sender.sendMessage(ChatColor.RED + "Nur im Spiel verwendbar.");
            return true;
        }

        // ✅ Nur OPs oder Admins mit Berechtigung dürfen GUI öffnen
        if (!p.isOp() && !p.hasPermission("lucacrafter.perms.manage")) {
            p.sendMessage(ChatColor.RED + "Du hast keine Rechte, das Menü zu öffnen!");
            return true;
        }

        new PermsMainGUI(plugin).open(p);
        return true;
    }
}
