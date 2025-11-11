package de.luca.plugin;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class AntiGriefCommand implements CommandExecutor {

    private final LucaCrafterPlugin plugin;

    public AntiGriefCommand(LucaCrafterPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!sender.hasPermission("plugin.antigrief")) {
            sender.sendMessage(ChatColor.RED + "❌ Keine Berechtigung!");
            return true;
        }

        if (args.length < 2 || !args[0].equalsIgnoreCase("creeper")) {
            sender.sendMessage(ChatColor.YELLOW + "⚙️ Nutzung: /antigrief creeper <on/off>");
            return true;
        }

        boolean enable = args[1].equalsIgnoreCase("on");
        plugin.getConfig().set("antigrief.creeper-disable-blocks", enable);
        plugin.saveConfig();

        sender.sendMessage(ChatColor.GOLD + "💥 Creeper-Blockschaden wurde "
                + (enable ? ChatColor.GREEN + "deaktiviert" : ChatColor.RED + "aktiviert") + ChatColor.GOLD + "!");
        return true;
    }
}
