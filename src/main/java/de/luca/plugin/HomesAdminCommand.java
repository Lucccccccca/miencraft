package de.luca.plugin;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class HomesAdminCommand implements CommandExecutor {

    private final LucaCrafterPlugin plugin;

    public HomesAdminCommand(LucaCrafterPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {

        if (!(sender instanceof Player)) {
            sender.sendMessage("§cNur Admins im Spiel!");
            return true;
        }

        Player admin = (Player) sender;

        if (args.length != 1) {
            admin.sendMessage("§eVerwendung: §7/homesadmin <Name>");
            return true;
        }

        OfflinePlayer target = Bukkit.getOfflinePlayer(args[0]);

        admin.openInventory(new HomeAdminGUI(plugin, admin, target).getInventory());
        return true;
    }
}
