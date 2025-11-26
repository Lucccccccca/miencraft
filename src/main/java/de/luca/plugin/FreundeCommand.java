package de.luca.plugin;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.*;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class FreundeCommand implements CommandExecutor, TabCompleter {

    private final LucaCrafterPlugin plugin;

    public FreundeCommand(LucaCrafterPlugin plugin) {
        this.plugin = plugin;
    }

    // ----- /freunde -----
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {

        if (!(sender instanceof Player)) {
            sender.sendMessage("Nur im Spiel verwendbar.");
            return true;
        }

        Player p = (Player) sender;
        FriendManager fm = plugin.getFriendManager();

        if (args.length == 0) {
            // Direkt GUI öffnen
            p.openInventory(FreundeMainGUI.build(plugin, p));
            return true;
        }

        String sub = args[0].toLowerCase();

        // --- Text-Menü im Chat (falls gewünscht: /freunde hilfe) ---
        if (sub.equals("hilfe") || sub.equals("help") || sub.equals("?")) {
            p.sendMessage("§b—— §eFreunde-System §b——");
            p.sendMessage("§e/freunde§7  – öffnet das Freunde-Menü (GUI)");
            p.sendMessage("§e/freunde hinzufügen <Name>§7  – sendet eine Freundschaftsanfrage");
            p.sendMessage("§e/freunde entfernen <Name>§7  – entfernt einen Freund");
            p.sendMessage("§e/freunde blockieren <Name>§7 – blockiert einen Spieler");
            p.sendMessage("§e/freunde entblocken <Name>§7 – hebt Blockierung auf");
            p.sendMessage("§e/freunde anfragen§7  – zeigt offene Anfragen (GUI)");
            p.sendMessage("§e/freunde liste§7     – zeigt Freunde-Liste (GUI)");
            return true;
        }

        // --- GUI-Shortcuts ---
        if (sub.equals("anfragen")) {
            p.openInventory(FriendListGUI.build(plugin, p, FriendListType.REQUESTS));
            return true;
        }
        if (sub.equals("liste") || sub.equals("online") || sub.equals("offline") || sub.equals("favoriten") || sub.equals("blockiert")) {
            FriendListType type = FriendListType.ONLINE;
            if (sub.equals("offline")) type = FriendListType.OFFLINE;
            if (sub.equals("favoriten")) type = FriendListType.FAVORITES;
            if (sub.equals("blockiert")) type = FriendListType.BLOCKED;
            if (sub.equals("liste")) type = FriendListType.ONLINE;
            if (sub.equals("anfragen")) type = FriendListType.REQUESTS;
            p.openInventory(FriendListGUI.build(plugin, p, type));
            return true;
        }

        // --- Aktionen mit Namen ---
        if (args.length < 2) {
            p.sendMessage("§eVerwendung:");
            p.sendMessage("§e/freunde hinzufügen <Name>");
            p.sendMessage("§e/freunde entfernen <Name>");
            p.sendMessage("§e/freunde blockieren <Name>");
            p.sendMessage("§e/freunde entblocken <Name>");
            return true;
        }

        String name = args[1];
        OfflinePlayer target = Bukkit.getOfflinePlayer(name);
        if (target == null || (target.getName() == null && !target.hasPlayedBefore())) {
            p.sendMessage("§cSpieler nicht gefunden.");
            return true;
        }

        UUID me = p.getUniqueId();
        UUID tu = target.getUniqueId();

        switch (sub) {
            case "hinzufügen": {
                if (fm.areFriends(me, tu)) {
                    p.sendMessage("§eIhr seid bereits Freunde.");
                    return true;
                }
                if (fm.isBlocked(me, tu) || fm.isBlocked(tu, me)) {
                    p.sendMessage("§cEine Freundschaft ist nicht möglich (Blockiert).");
                    return true;
                }
                if (fm.sendFriendRequest(me, tu)) {
                    p.sendMessage("§aFreundschaftsanfrage an §e" + target.getName() + " §agesendet.");
                    Player onlineTarget = target.getPlayer();
                    if (onlineTarget != null && onlineTarget.isOnline()) {
                        onlineTarget.sendMessage("§aDu hast eine Freundschaftsanfrage von §e" + p.getName() + "§a erhalten.");
                        onlineTarget.sendMessage("§7Öffne mit §e/freunde §7das Freunde-Menü.");
                    }
                } else {
                    p.sendMessage("§cKonnte keine Anfrage senden (evtl. schon offen?).");
                }
                return true;
            }
            case "entfernen": {
                if (fm.removeFriend(me, tu)) {
                    p.sendMessage("§cDu bist nicht mehr mit §e" + target.getName() + " §cbefreundet.");
                } else {
                    p.sendMessage("§eIhr seid keine Freunde.");
                }
                return true;
            }
            case "blockieren": {
                if (fm.block(me, tu)) {
                    p.sendMessage("§cDu hast §e" + target.getName() + " §cblockiert.");
                } else {
                    p.sendMessage("§cBlockieren fehlgeschlagen.");
                }
                return true;
            }
            case "entblocken": {
                if (fm.unblock(me, tu)) {
                    p.sendMessage("§aDu hast §e" + target.getName() + " §awieder entblockt.");
                } else {
                    p.sendMessage("§eDieser Spieler war nicht blockiert.");
                }
                return true;
            }
            default:
                p.sendMessage("§eUnbekannter Unterbefehl. Nutze §e/freunde hilfe");
                return true;
        }
    }

    // ----- Tab-Completion -----
    @Override
    public List<String> onTabComplete(CommandSender sender, Command cmd, String alias, String[] args) {
        List<String> out = new ArrayList<>();
        if (!(sender instanceof Player)) return out;

        if (args.length == 1) {
            String s = args[0].toLowerCase();
            String[] base = new String[] {
                    "hilfe","anfragen","liste","online","offline","favoriten","blockiert",
                    "hinzufügen","entfernen","blockieren","entblocken"
            };
            for (String b : base) if (b.startsWith(s)) out.add(b);
            return out;
        }

        if (args.length == 2) {
            // Namen vorschlagen (online)
            String s = args[1].toLowerCase();
            for (Player pl : Bukkit.getOnlinePlayers()) {
                if (pl.getName().toLowerCase().startsWith(s)) out.add(pl.getName());
            }
            return out;
        }
        return out;
    }
}
