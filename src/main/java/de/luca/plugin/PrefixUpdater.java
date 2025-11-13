package de.luca.plugin;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class PrefixUpdater implements Listener {

    private final LucaCrafterPlugin plugin;

    public PrefixUpdater(LucaCrafterPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        updatePrefix(event.getPlayer());
    }

    /** Prefix aus roles.yml anwenden */
    public void updatePrefix(Player p) {
        String role = plugin.getRoleManager().getRole(p.getUniqueId());
        String prefix = plugin.getRoleManager().getPrefix(role);

        String name = prefix + p.getName();
        p.setPlayerListName(name);

        // Optional: Chat formatieren (kannst du entfernen wenn unerwünscht)
        Bukkit.getServer().dispatchCommand(
                Bukkit.getConsoleSender(),
                "tellraw " + p.getName() + " {\"text\":\"\",\"extra\":[{\"text\":\"\"}]}"
        );
    }

    /** Alle aktualisieren */
    public void updateAll() {
        for (Player p : Bukkit.getOnlinePlayers()) updatePrefix(p);
    }
}
