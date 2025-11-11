package de.luca.plugin;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;

public class ServerSettingsGUI {

    private final LucaCrafterPlugin plugin;
    private final Player player;

    public ServerSettingsGUI(Player player, LucaCrafterPlugin plugin) {
        this.plugin = plugin;
        this.player = player;
    }

    public void open() {
        Inventory inv = Bukkit.createInventory(null, 27, "§b⚙️ Server-Einstellungen");

        boolean farmProtect = plugin.getConfigManager().isFarmProtectEnabled();
        boolean antiCreeper = plugin.getConfigManager().isAntiCreeperEnabled();
        boolean tnt = plugin.getConfigManager().isTntBlockDamageEnabled();
        boolean pvp = plugin.getConfigManager().isPvpEnabled();
        boolean mobGrief = plugin.getConfigManager().isMobGriefingEnabled();

        inv.setItem(10, createItem(Material.WHEAT, farmProtect ? "§a🌾 Farm-Protect: AN" : "§c🌾 Farm-Protect: AUS", List.of("§7Schützt Felder vor Fremdzerstörung")));
        inv.setItem(11, createItem(Material.CREEPER_HEAD, antiCreeper ? "§a💣 Anti-Creeper: AN" : "§c💣 Anti-Creeper: AUS", List.of("§7Creeper-Schäden verhindern")));
        inv.setItem(12, createItem(Material.TNT, tnt ? "§a🔥 TNT-Schaden: AN" : "§c🔥 TNT-Schaden: AUS", List.of("§7TNT-Blockschäden erlauben oder blocken")));
        inv.setItem(13, createItem(Material.IRON_SWORD, pvp ? "§a⚔️ PvP: AN" : "§c⚔️ PvP: AUS", List.of("§7Spieler-vs-Spieler aktivieren oder deaktivieren")));
        inv.setItem(14, createItem(Material.ZOMBIE_HEAD, mobGrief ? "§a👾 Mob-Griefing: AN" : "§c👾 Mob-Griefing: AUS", List.of("§7Mobs dürfen Blöcke zerstören oder nicht")));

        inv.setItem(26, createItem(Material.BARRIER, "§cSchließen", List.of("§7Menü schließen")));

        player.openInventory(inv);
    }

    private ItemStack createItem(Material mat, String name, List<String> lore) {
        ItemStack item = new ItemStack(mat);
        ItemMeta meta = item.getItemMeta();
        if (meta == null) return item;

        meta.setDisplayName(name);
        meta.setLore(lore);
        item.setItemMeta(meta);
        return item;
    }
}
