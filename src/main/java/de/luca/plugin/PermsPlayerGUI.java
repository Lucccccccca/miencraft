package de.luca.plugin;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.*;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class PermsPlayerGUI {

    public static final String TITLE_PREFIX = "§aSpieler: ";
    private final LucaCrafterPlugin plugin;
    private final UUID targetId;

    public PermsPlayerGUI(LucaCrafterPlugin plugin, UUID targetId) {
        this.plugin = plugin;
        this.targetId = targetId;
    }

    public void open(Player viewer) {
        OfflinePlayer target = Bukkit.getOfflinePlayer(targetId);
        Inventory inv = Bukkit.createInventory(null, 27, TITLE_PREFIX + target.getName());

        String currentRole = plugin.getRoleManager().getRole(targetId);
        List<String> roles = plugin.getRoleManager().getAllRoles();

        // Anzeige aktuelle Rolle
        inv.setItem(10, simple(Material.NAME_TAG, "§bAktuelle Rolle", "§f" + currentRole));

        // Rolle wechseln (vor/zurück)
        inv.setItem(11, simple(Material.ARROW, "§7Vorherige Rolle", "§8Klicken zum Blättern"));
        inv.setItem(15, simple(Material.ARROW, "§7Nächste Rolle", "§8Klicken zum Blättern"));

        // Setzen
        inv.setItem(13, simple(Material.LIME_DYE, "§aRolle zuweisen", "§7Speichert die Auswahl"));

        // Rolle-Permissions ansehen/bearbeiten
        inv.setItem(16, simple(Material.BOOK, "§eRollen-Permissions", "§7Perms der Rolle ansehen/ändern"));

        // Zurück
        inv.setItem(22, simple(Material.BARRIER, "§cZurück", "§7Zur Spielerliste"));

        viewer.openInventory(inv);
        // aktuelle Rolle im Listener per Slotindex verwaltet
        PermsGUIListener.PlayerState ps = PermsGUIListener.getState(viewer);
        ps.targetId = targetId;
        ps.roleCursor = roles.indexOf(currentRole) < 0 ? 0 : roles.indexOf(currentRole);
    }

    private ItemStack simple(Material m, String name, String loreLine) {
        ItemStack it = new ItemStack(m);
        ItemMeta im = it.getItemMeta();
        im.setDisplayName(name);
        if (loreLine != null) {
            List<String> lore = new ArrayList<>();
            lore.add(loreLine);
            im.setLore(lore);
        }
        it.setItemMeta(im);
        return it;
    }
}
