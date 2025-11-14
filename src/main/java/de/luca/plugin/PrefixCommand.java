package de.luca.plugin;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class PrefixCommand implements CommandExecutor {

    private final LucaCrafterPlugin plugin;

    public PrefixCommand(LucaCrafterPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("Dieser Befehl ist nur für Spieler.");
            return true;
        }

        if (!player.hasPermission("lucacrafter.tab.prefix")) {
            player.sendMessage("§cDu hast keine Rechte für dieses Menü.");
            return true;
        }

        PrefixGUI.open(plugin, player);
        return true;
    }
}
