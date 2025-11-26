package de.luca.plugin;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.*;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.UUID;

public class FreundeGUIListener implements Listener {

    private final LucaCrafterPlugin plugin;

    public FreundeGUIListener(LucaCrafterPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onClick(InventoryClickEvent e) {

        if (e.getCurrentItem() == null || e.getCurrentItem().getItemMeta() == null)
            return;

        Player p = (Player) e.getWhoClicked();
        String title = e.getView().getTitle();
        ItemStack clicked = e.getCurrentItem();
        Material mat = clicked.getType();
        String name = ChatColor.stripColor(clicked.getItemMeta().getDisplayName());

        // ❗ Nur blockieren, wenn es wirklich unsere Freunde-GUIs sind
        boolean isFreundeGUI =
                title.equals("§bFreunde-Menü") ||
                title.equals("§aFreund hinzufügen") ||
                title.equals("§aOnline-Spieler") ||
                title.startsWith("§bFreunde:") ||
                title.startsWith("§bFreund: §e");

        if (!isFreundeGUI)
            return; // Spielerinventar bleibt unberührt!

        // Ab hier → unsere GUIs blockieren
        e.setCancelled(true);

        // ============= HAUPTMENÜ =============
        if (title.equals("§bFreunde-Menü")) {

            if (name.contains("Freund hinzufügen")) {
                p.openInventory(FreundeAddGUI.build(p));
                return;
            }
            if (name.contains("Freunde (Liste)")) {
                p.openInventory(FriendListGUI.build(plugin, p, FriendListType.ONLINE));
                return;
            }
            if (name.contains("Freunde Online")) {
                p.openInventory(FriendListGUI.build(plugin, p, FriendListType.ONLINE));
                return;
            }
            if (name.contains("Freunde Offline")) {
                p.openInventory(FriendListGUI.build(plugin, p, FriendListType.OFFLINE));
                return;
            }
            if (name.contains("Favoriten")) {
                p.openInventory(FriendListGUI.build(plugin, p, FriendListType.FAVORITES));
                return;
            }
            if (name.contains("Blockierte")) {
                p.openInventory(FriendListGUI.build(plugin, p, FriendListType.BLOCKED));
                return;
            }
        }

        // ============= FREUND HINZUFÜGEN =============
        if (title.equals("§aFreund hinzufügen")) {

            if (name.contains("Spieler online")) {
                p.openInventory(FreundeAddOnlineGUI.build());
                return;
            }

            if (name.contains("Manuell eingeben")) {
                plugin.getManualNameInput().start(p.getUniqueId());
                p.closeInventory();
                p.sendMessage("§bBitte gib den Namen ein (§7'abbrechen' zum Stoppen§b).");
                return;
            }

            if (name.contains("Zurück")) {
                p.openInventory(FreundeMainGUI.build(plugin, p));
                return;
            }
        }

        // ============= ONLINE-SPIELER =============
        if (title.equals("§aOnline-Spieler")) {

            if (mat == Material.ARROW && name.equals("Zurück")) {
                p.openInventory(FreundeAddGUI.build(p));
                return;
            }

            if (mat == Material.PLAYER_HEAD) {

                OfflinePlayer target = Bukkit.getOfflinePlayer(name);
                if (target == null) return;

                UUID me = p.getUniqueId();
                UUID tu = target.getUniqueId();

                if (plugin.getFriendManager().sendFriendRequest(me, tu)) {
                    p.sendMessage("§aFreundschaftsanfrage an §e" + target.getName() + " §agesendet.");
                } else {
                    p.sendMessage("§cAnfrage konnte nicht gesendet werden.");
                }
                return;
            }
        }

        // ============= FREUND-LISTEN =============
        if (title.startsWith("§bFreunde:")) {

            if (mat == Material.ARROW && name.equals("Zurück")) {
                p.openInventory(FreundeMainGUI.build(plugin, p));
                return;
            }

            if (mat == Material.PLAYER_HEAD) {
                OfflinePlayer op = Bukkit.getOfflinePlayer(name);
                if (op == null || op.getName() == null) return;

                UUID tu = op.getUniqueId();
                UUID me = p.getUniqueId();

                if (title.contains("Anfragen")) {

                    if (e.isLeftClick()) {
                        plugin.getFriendManager().acceptRequest(me, tu);
                        p.openInventory(FriendListGUI.build(plugin, p, FriendListType.REQUESTS));
                        return;
                    }
                    if (e.isRightClick()) {
                        plugin.getFriendManager().denyRequest(me, tu);
                        p.openInventory(FriendListGUI.build(plugin, p, FriendListType.REQUESTS));
                        return;
                    }
                }

                if (title.contains("Blockierte")) {

                    if (e.isRightClick()) {
                        plugin.getFriendManager().unblock(me, tu);
                        p.openInventory(FriendListGUI.build(plugin, p, FriendListType.BLOCKED));
                        return;
                    }
                }

                // Optionen-Menü
                if (e.isLeftClick() && !e.isShiftClick()) {
                    p.openInventory(FreundeOptionsGUI.build(plugin, tu));
                    return;
                }

                if (e.isRightClick() && e.isShiftClick()) {
                    plugin.getFriendManager().removeFriend(me, tu);
                    p.openInventory(FriendListGUI.build(plugin, p, FriendListType.ONLINE));
                    return;
                }

                if (e.isLeftClick() && e.isShiftClick()) {
                    plugin.getFriendManager().block(me, tu);
                    p.openInventory(FriendListGUI.build(plugin, p, FriendListType.BLOCKED));
                    return;
                }
            }
        }

        // ============= FREUND-OPTIONEN =============
        if (title.startsWith("§bFreund: §e")) {

            String friendName = ChatColor.stripColor(title.replace("§bFreund: §e", ""));
            OfflinePlayer op = Bukkit.getOfflinePlayer(friendName);
            if (op == null || op.getName() == null) return;

            UUID me = p.getUniqueId();
            UUID tu = op.getUniqueId();

            if (name.contains("Homes ansehen")) {
                p.sendMessage("§e(Homes-GUI hier einbauen.)");
                return;
            }

            if (name.contains("Favorit")) {
                plugin.getFriendManager().toggleFavorite(me, tu);
                p.openInventory(FreundeOptionsGUI.build(plugin, tu));
                return;
            }

            if (name.contains("Freund entfernen")) {
                plugin.getFriendManager().removeFriend(me, tu);
                p.openInventory(FriendListGUI.build(plugin, p, FriendListType.ONLINE));
                return;
            }

            if (name.contains("Blockieren")) {
                plugin.getFriendManager().block(me, tu);
                p.openInventory(FriendListGUI.build(plugin, p, FriendListType.BLOCKED));
                return;
            }

            if (name.contains("Zurück")) {
                p.openInventory(FriendListGUI.build(plugin, p, FriendListType.ONLINE));
                return;
            }
        }
    }
}
