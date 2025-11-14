package de.luca.plugin;

import de.luca.plugin.LucaCrafterPlugin;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class DeleteHomeCommand implements CommandExecutor {

    private final LucaCrafterPlugin plugin;

    public DeleteHomeCommand(LucaCrafterPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player p)) return true;

        if (args.length != 1) {
            p.sendMessage("§cBenutzung: /delhome <Name>");
            return true;
        }

        plugin.getHomeManager().deleteHome(p.getUniqueId(), args[0]);
        p.sendMessage("§cHome gelöscht.");

        return true;
    }
}
