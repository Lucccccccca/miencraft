package de.luca.plugin;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class DelHomeCommand implements CommandExecutor {

    private final LucaCrafterPlugin plugin;

    public DelHomeCommand(LucaCrafterPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {

        if (!(sender instanceof Player)) {
            sender.sendMessage("§cNur Spieler können Homes löschen.");
            return true;
        }

        Player p = (Player) sender;

        if (args.length != 1) {
            p.sendMessage("§eVerwendung: §7/delhome <Name>");
            return true;
        }

        String name = args[0].toLowerCase();

        if (!plugin.getHomeManager().hasHome(p.getUniqueId(), name)) {
            p.sendMessage("§cDieses Home existiert nicht!");
            return true;
        }

        plugin.getHomeManager().deleteHome(p.getUniqueId(), name);
        p.sendMessage("§aHome §e'" + name + "' §awurde gelöscht!");

        return true;
    }
}
