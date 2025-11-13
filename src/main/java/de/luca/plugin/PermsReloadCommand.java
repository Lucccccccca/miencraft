package de.luca.plugin;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class PermsReloadCommand implements CommandExecutor {

    private final LucaCrafterPlugin plugin;

    public PermsReloadCommand(LucaCrafterPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender s, Command cmd, String label, String[] args) {

        if (!s.hasPermission("lucacrafter.perms.manage")) {
            s.sendMessage(ChatColor.RED + "Keine Rechte!");
            return true;
        }

        plugin.getRoleManager().reloadRoles();
        plugin.getPermissionUpdater().refreshAllPlayers();
        plugin.getPrefixUpdater().updateAll();

        s.sendMessage(ChatColor.GREEN + "✔ Rollen, Rechte & Prefixe neu geladen!");
        return true;
    }
}
