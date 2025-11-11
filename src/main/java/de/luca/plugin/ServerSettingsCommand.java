package de.luca.plugin;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ServerSettingsCommand implements CommandExecutor {

    private final LucaCrafterPlugin plugin;

    public ServerSettingsCommand(LucaCrafterPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player p)) {
            sender.sendMessage("§cNur Spieler können dieses Menü öffnen!");
            return true;
        }

        if (!p.isOp()) {
            p.sendMessage("§cNur Admins dürfen dieses Menü öffnen!");
            return true;
        }

        new ServerSettingsGUI(p, plugin).open();
        return true;
    }
}
