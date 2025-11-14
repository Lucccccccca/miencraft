package de.luca.plugin;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class HomeCommand implements CommandExecutor {

    private final LucaCrafterPlugin plugin;

    public HomeCommand(LucaCrafterPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {

        if (!(sender instanceof Player)) {
            sender.sendMessage("§cNur Spieler können dieses Kommando nutzen.");
            return true;
        }

        Player p = (Player) sender;
        p.openInventory(new HomeMainGUI(plugin, p).getInventory());
        return true;
    }
}
