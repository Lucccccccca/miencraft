package de.luca.plugin;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CraftGUICommand implements CommandExecutor {

    private final LucaCrafterPlugin plugin;

    public CraftGUICommand(LucaCrafterPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player p)) {
            sender.sendMessage("Nur im Spiel nutzbar.");
            return true;
        }
        new CraftMainGUI(plugin).open(p);
        return true;
    }
}
