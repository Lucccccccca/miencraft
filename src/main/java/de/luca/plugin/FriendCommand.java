package de.luca.plugin;

import de.luca.plugin.FriendMainGUI;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.*;
import org.bukkit.entity.Player;

import java.util.UUID;

public class FriendCommand implements CommandExecutor {

    private final LucaCrafterPlugin plugin;

    public FriendCommand(LucaCrafterPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {

        if (!(sender instanceof Player)) {
            sender.sendMessage("Nur im Spiel verwendbar.");
            return true;
        }

        Player p = (Player) sender;
        UUID uuid = p.getUniqueId();
        FriendManager fm = plugin.getFriendManager();

        if (cmd.getName().equalsIgnoreCase("friends")) {
            // Öffnet Haupt-Freunde-Menü (GUI)
            p.openInventory(new FriendMainGUI(plugin, p).getInventory());
            return true;
        }

        // /friend <sub> <Name>
        if (args.length < 2) {
            p.sendMessage("§eVerwendung:");
            p.sendMessage("§e/friend add <Name> §7- Anfrage senden");
            p.sendMessage("§e/friend remove <Name> §7- Freund entfernen");
            p.sendMessage("§e/friend block <Name> §7- Spieler blockieren");
            p.sendMessage("§e/friend unblock <Name> §7- Blockierung aufheben");
            p.sendMessage("§e/friends §7- Freunde-Menü öffnen");
            return true;
        }

        String sub = args[0].toLowerCase();
        String name = args[1];

        OfflinePlayer target = Bukkit.getOfflinePlayer(name);
        if (target == null || (target.getName() == null && !target.hasPlayedBefore())) {
            p.sendMessage("§cSpieler nicht gefunden.");
            return true;
        }

        UUID tUUID = target.getUniqueId();

        if (sub.equals("add")) {
            if (fm.areFriends(uuid, tUUID)) {
                p.sendMessage("§eIhr seid bereits Freunde.");
                return true;
            }
            if (fm.isBlocked(uuid, tUUID) || fm.isBlocked(tUUID, uuid)) {
                p.sendMessage("§cEine Freundschaft ist nicht möglich (Blockiert).");
                return true;
            }
            if (fm.sendFriendRequest(uuid, tUUID)) {
                p.sendMessage("§aFreundschaftsanfrage an §e" + target.getName() + " §agesendet.");
                Player onlineTarget = target.getPlayer();
                if (onlineTarget != null && onlineTarget.isOnline()) {
                    onlineTarget.sendMessage("§aDu hast eine Freundschaftsanfrage von §e" + p.getName() + "§a erhalten.");
                    onlineTarget.sendMessage("§7Öffne mit §e/friends §7das Freunde-Menü.");
                }
            } else {
                p.sendMessage("§cKonnte keine Anfrage senden (evtl. schon offen?).");
            }
            return true;
        }

        if (sub.equals("remove")) {
            if (fm.removeFriend(uuid, tUUID)) {
                p.sendMessage("§cDu bist nicht mehr mit §e" + target.getName() + " §cbefreundet.");
            } else {
                p.sendMessage("§eIhr seid keine Freunde.");
            }
            return true;
        }

        if (sub.equals("block")) {
            if (fm.block(uuid, tUUID)) {
                p.sendMessage("§cDu hast §e" + target.getName() + " §cblockiert.");
            } else {
                p.sendMessage("§cBlockieren fehlgeschlagen.");
            }
            return true;
        }

        if (sub.equals("unblock")) {
            if (fm.unblock(uuid, tUUID)) {
                p.sendMessage("§aDu hast §e" + target.getName() + " §awieder entblockt.");
            } else {
                p.sendMessage("§eDieser Spieler war nicht blockiert.");
            }
            return true;
        }

        p.sendMessage("§eUnbekannter Unterbefehl. Nutze §e/friend add/remove/block/unblock");
        return true;
    }
}
