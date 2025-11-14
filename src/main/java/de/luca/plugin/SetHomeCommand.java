package de.luca.plugin;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SetHomeCommand implements CommandExecutor {

    private final LucaCrafterPlugin plugin;

    public SetHomeCommand(LucaCrafterPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {

        if (!(sender instanceof Player)) {
            sender.sendMessage("§cNur Spieler können Homes setzen.");
            return true;
        }

        Player p = (Player) sender;

        if (args.length != 1) {
            p.sendMessage("§eVerwendung: §7/sethome <Name>");
            return true;
        }

        String name = args[0].toLowerCase();

        // Max Homes check
        int max = plugin.getConfigManager().getMaxHomes(p.getUniqueId());
        int current = plugin.getHomeManager().getHomes(p.getUniqueId()).size();
        if (current >= max) {
            p.sendMessage("§cDu hast die maximale Anzahl an Homes erreicht! (§6" + max + "§c)");
            return true;
        }

        plugin.getHomeManager().setHome(p.getUniqueId(), name, p.getLocation());
        p.sendMessage("§aHome §e'" + name + "' §awurde gespeichert!");

        return true;
    }
}
