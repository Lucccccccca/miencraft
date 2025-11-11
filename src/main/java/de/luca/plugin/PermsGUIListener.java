package de.luca.plugin;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.*;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.ItemStack;

import java.util.*;

public class PermsGUIListener implements Listener {

    private final LucaCrafterPlugin plugin;

    public PermsGUIListener(LucaCrafterPlugin plugin) {
        this.plugin = plugin;
    }

    // === 🧠 Spielerstatus speichern ===
    public static class PlayerState {
        public UUID targetId = null;
        public int roleCursor = 0;
        public boolean awaitingRoleCreate = false;
        public boolean awaitingRoleDelete = false;
        public String roleToEdit = null;
    }

    private static final Map<UUID, PlayerState> state = new HashMap<>();

    public static PlayerState getState(Player p) {
        return state.computeIfAbsent(p.getUniqueId(), k -> new PlayerState());
    }

    // === 🖱 GUI Click Handling ===
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onClick(InventoryClickEvent e) {
        if (!(e.getWhoClicked() instanceof Player p)) return;
        if (e.getView().getTitle() == null || e.getClickedInventory() == null) return;

        String title = e.getView().getTitle();

        // 🔒 Nur GUIs des Permissions-Systems
        if (!(title.startsWith("Rolle:") || title.equals(PermsMainGUI.TITLE)
                || title.equals(PermsRoleListGUI.TITLE)
                || title.startsWith(PermsPlayerGUI.TITLE_PREFIX)
                || title.startsWith(PermsRoleGUI.TITLE_PREFIX))) return;

        // 🚫 Kein Item verschieben oder Doppelklick-Trigger
        e.setCancelled(true);
        if (e.getClick().isShiftClick() || e.getClick().isKeyboardClick()) return;
        if (e.getClickedInventory().getType() == InventoryType.PLAYER) return;

        // 👮‍♂️ Permission check
        if (!p.isOp() && !p.hasPermission("lucacrafter.perms.manage")) {
            p.sendMessage(ChatColor.RED + "Keine Berechtigung, Änderungen vorzunehmen.");
            return;
        }

        ItemStack clicked = e.getCurrentItem();
        if (clicked == null || !clicked.hasItemMeta()) return;

        // === 🧾 Hauptmenü ===
        if (title.equals(PermsMainGUI.TITLE)) {
            switch (clicked.getType()) {
                case WRITABLE_BOOK -> {
                    PlayerState ps = getState(p);
                    ps.awaitingRoleCreate = true;
                    p.closeInventory();
                    p.sendMessage(ChatColor.AQUA + "✏ Gib im Chat den neuen Rollennamen ein (Abbruch: 'cancel').");
                }
                case ANVIL -> {
                    List<String> roles = plugin.getRoleManager().getAllRoles();
                    if (roles.isEmpty()) {
                        p.sendMessage(ChatColor.RED + "❌ Es gibt noch keine Rollen.");
                        return;
                    }
                    new PermsRoleListGUI(plugin).open(p);
                }
                case PLAYER_HEAD -> {
                    OfflinePlayer target = clicked.getItemMeta() instanceof org.bukkit.inventory.meta.SkullMeta sm ? sm.getOwningPlayer() : null;
                    if (target == null) return;
                    new PermsPlayerGUI(plugin, target.getUniqueId()).open(p);
                }
                case BARRIER -> p.closeInventory();
                case CLOCK -> new PermsMainGUI(plugin).open(p);
            }
            return;
        }

        // === 👤 Spieler-Menü ===
        if (title.startsWith(PermsPlayerGUI.TITLE_PREFIX)) {
            PlayerState ps = getState(p);
            List<String> roles = plugin.getRoleManager().getAllRoles();
            if (roles.isEmpty()) {
                p.sendMessage(ChatColor.RED + "❌ Keine Rollen definiert.");
                return;
            }

            int slot = e.getRawSlot();
            switch (slot) {
                case 11 -> { // ← zurück
                    ps.roleCursor = (ps.roleCursor - 1 + roles.size()) % roles.size();
                    p.sendMessage(ChatColor.GRAY + "Aktuelle Rolle: " + ChatColor.AQUA + roles.get(ps.roleCursor));
                }
                case 15 -> { // → weiter
                    ps.roleCursor = (ps.roleCursor + 1) % roles.size();
                    p.sendMessage(ChatColor.GRAY + "Aktuelle Rolle: " + ChatColor.AQUA + roles.get(ps.roleCursor));
                }
                case 13 -> { // Rolle setzen
                    if (ps.targetId == null) return;
                    String role = roles.get(ps.roleCursor);
                    plugin.getRoleManager().setRole(ps.targetId, role);
                    p.sendMessage(ChatColor.GREEN + "✔ Rolle gesetzt: " + role);
                    Bukkit.getScheduler().runTaskLater(plugin, () ->
                            new PermsPlayerGUI(plugin, ps.targetId).open(p), 2L);
                }
                case 16 -> { // Role-Edit öffnen
                    String role = roles.get(ps.roleCursor);
                    ps.roleToEdit = role;
                    new PermsRoleGUI(plugin, role).open(p);
                }
                case 22 -> new PermsMainGUI(plugin).open(p); // zurück
            }
            return;
        }

        // === 🧱 Rollen-Permissions ===
        if (title.startsWith(PermsRoleGUI.TITLE_PREFIX)) {
            String role = ChatColor.stripColor(title.substring(PermsRoleGUI.TITLE_PREFIX.length()));
            int slot = e.getRawSlot();

            switch (slot) {
                case 49 -> new PermsMainGUI(plugin).open(p);
                case 53 -> {
                    p.sendMessage(ChatColor.GREEN + "✔ Änderungen gespeichert.");
                    new PermsMainGUI(plugin).open(p);
                }
                default -> {
                    List<String> nodes = PermsRoleGUI.KNOWN_NODES;
                    if (slot < 0 || slot >= nodes.size()) return;
                    String node = nodes.get(slot);
                    Set<String> perms = plugin.getRoleManager().getRolePermissions(role);

                    boolean nowEnabled = !perms.contains(node);
                    if (nowEnabled) {
                        plugin.getRoleManager().addPermissionToRole(role, node);
                        p.sendMessage(ChatColor.GREEN + "✔ Erlaubt: " + node);
                    } else {
                        plugin.getRoleManager().removePermissionFromRole(role, node);
                        p.sendMessage(ChatColor.YELLOW + "❌ Entzogen: " + node);
                    }

                    Bukkit.getScheduler().runTaskLater(plugin, () ->
                            new PermsRoleGUI(plugin, role).open(p), 2L);
                }
            }
            return;
        }

        // === 📜 Rollenliste ===
        if (title.equals(PermsRoleListGUI.TITLE)) {
            int slot = e.getRawSlot();
            if (slot == 49) {
                new PermsMainGUI(plugin).open(p);
                return;
            }
            if (slot == 53) {
                PlayerState ps = getState(p);
                ps.awaitingRoleDelete = true;
                p.closeInventory();
                p.sendMessage(ChatColor.RED + "❗ Gib im Chat die zu löschende Rolle ein (Abbruch: 'cancel').");
                return;
            }

            ItemStack it = e.getCurrentItem();
            if (it != null && it.getType() == Material.PAPER && it.getItemMeta() != null) {
                String name = ChatColor.stripColor(it.getItemMeta().getDisplayName());
                new PermsRoleGUI(plugin, name).open(p);
            }
        }
    }

    // === 💬 Chat-Eingaben (Rolle erstellen / löschen) ===
    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    public void onChat(AsyncPlayerChatEvent e) {
        Player p = e.getPlayer();
        PlayerState ps = getState(p);
        String msg = e.getMessage().trim();

        if (ps.awaitingRoleCreate) {
            e.setCancelled(true);
            Bukkit.getScheduler().runTask(plugin, () -> {
                if (msg.equalsIgnoreCase("cancel")) {
                    p.sendMessage(ChatColor.GRAY + "Abgebrochen.");
                } else {
                    if (plugin.getRoleManager().createRole(msg)) {
                        p.sendMessage(ChatColor.GREEN + "✔ Rolle erstellt: " + msg);
                    } else {
                        p.sendMessage(ChatColor.RED + "❌ Rolle existiert bereits!");
                    }
                }
                ps.awaitingRoleCreate = false;
                new PermsMainGUI(plugin).open(p);
            });
            return;
        }

        if (ps.awaitingRoleDelete) {
            e.setCancelled(true);
            Bukkit.getScheduler().runTask(plugin, () -> {
                if (msg.equalsIgnoreCase("cancel")) {
                    p.sendMessage(ChatColor.GRAY + "Abgebrochen.");
                } else {
                    if (plugin.getRoleManager().deleteRole(msg)) {
                        p.sendMessage(ChatColor.YELLOW + "🗑 Rolle gelöscht: " + msg);
                    } else {
                        p.sendMessage(ChatColor.RED + "❌ Keine solche Rolle gefunden.");
                    }
                }
                ps.awaitingRoleDelete = false;
                new PermsMainGUI(plugin).open(p);
            });
        }
    }
}
