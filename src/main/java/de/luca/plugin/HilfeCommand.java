package de.luca.plugin;

import org.bukkit.command.*;
import org.bukkit.entity.Player;

public class HilfeCommand implements CommandExecutor {
    private final LucaCrafterPlugin plugin;

    public HilfeCommand(LucaCrafterPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {

        if (!(sender instanceof Player)) {
            sender.sendMessage("Nur im Spiel verfügbar.");
            return true;
        }

        Player p = (Player) sender;

        p.openInventory(HelpMainGUI.build(plugin, p));
        return true;
    }
}
