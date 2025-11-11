package de.luca.plugin;

import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.List;
import java.util.Random;

public class JoinListener implements Listener {

    private final Random random = new Random();

    // 💬 Zufällige Join-Nachrichten mit Farben & Emojis
    private final List<String> joinMessages = List.of(
            ChatColor.GOLD + "hat den Ofen angeschmissen 🔥",
            ChatColor.AQUA + "fliegt jetzt los 🚀",
            ChatColor.GREEN + "baut wieder Unsinn 🧱",
            ChatColor.LIGHT_PURPLE + "sucht erstmal Kaffee ☕",
            ChatColor.YELLOW + "macht jetzt ernst 💪",
            ChatColor.RED + "ist auf Krawall gebürstet 😈",
            ChatColor.BLUE + "hat den Server gefunden 🌍",
            ChatColor.DARK_AQUA + "kommt mit guten Vibes 😎",
            ChatColor.DARK_GREEN + "bringt Glück 🍀",
            ChatColor.GOLD + "kommt aus dem Nether zurück 🔥",
            ChatColor.LIGHT_PURPLE + "hat heute was vor 💫",
            ChatColor.DARK_PURPLE + "hat die Macht gespürt ⚡",
            ChatColor.AQUA + "ist wieder da – Applaus bitte 👏",
            ChatColor.GREEN + "kommt, um Chaos zu stiften 💥",
            ChatColor.GOLD + "will nur kurz was testen 😏",
            ChatColor.RED + "hat TNT gesehen 💣",
            ChatColor.YELLOW + "ist bereit für Abenteuer 🗺️",
            ChatColor.BLUE + "hat das Licht gesehen 💡",
            ChatColor.DARK_RED + "kommt mit dunkler Energie 🌑",
            ChatColor.WHITE + "hat einfach Lust auf Blöcke 🧱"
    );

    // 🚪 Zufällige Leave-Nachrichten mit Farben & Emojis
    private final List<String> leaveMessages = List.of(
            ChatColor.GRAY + "hat den Server verlassen, um Kaffee zu holen ☕",
            ChatColor.DARK_PURPLE + "hat sich heimlich davongeschlichen 😏",
            ChatColor.RED + "ist vom Server verschwunden 💨",
            ChatColor.AQUA + "hat sich ausgeloggt, um zu chillen 😴",
            ChatColor.GOLD + "musste leider weg, kommt aber wieder ✌️",
            ChatColor.DARK_GREEN + "verlässt den Server für frische Luft 🌿",
            ChatColor.YELLOW + "geht AFK – für immer? 🤔",
            ChatColor.BLUE + "macht erstmal Pause 💤",
            ChatColor.LIGHT_PURPLE + "geht auf eine geheime Mission 🕵️",
            ChatColor.WHITE + "hat sich leise verabschiedet 👋"
    );

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        String playerName = ChatColor.GOLD + event.getPlayer().getName() + ChatColor.RESET;
        String randomText = joinMessages.get(random.nextInt(joinMessages.size()));
        event.setJoinMessage(ChatColor.GRAY + "✨ " + playerName + " " + randomText);
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        String playerName = ChatColor.GOLD + event.getPlayer().getName() + ChatColor.RESET;
        String randomText = leaveMessages.get(random.nextInt(leaveMessages.size()));
        event.setQuitMessage(ChatColor.DARK_GRAY + "🚪 " + playerName + " " + randomText);
    }
}
