package de.luca.plugin;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.List;
import java.util.Random;

public class JoinListener implements Listener {

    private final Random random = new Random();

    // ⭐ Zufällige JOIN-Nachrichten
    private final List<String> joinMessages = List.of(

            // 🤣 Lustig
            "ist bereit für absolute Chaos-Architektur 🤡",
            "kommt rein wie ein Creeper in dein Haus 💥",
            "fragt sich, warum der Server so gut riecht 🍞",
            "hat den Bauplan für das Ender-Chaos dabei 📐😈",
            "hat die Tür offen gelassen — oops 😳🚪",
            "hat mehr Items verloren als XP verdient 😂",
            "hat aus Versehen ein Dorf verkauft 🏘️💸",
            "ist AFK bevor er überhaupt joined 💤",
            "denkt immer noch, dass Dirt selten ist 🟫⭐",

            // 😈 Gefährlich
            "hat TNT in der Hand — rennt lieber 💣🔥",
            "kommt mit Wut und Wither-Power zurück ☠️💀",
            "hat heute keine Gnade 😡⚡",
            "hat Lava-Eimer geladen 🪣🔥",
            "spawnt heute vielleicht den Wither… vielleicht 😈",
            "kommt mit der Macht des Enderdrachens 🐉💫",

            // 😎 Cool
            "ist mit maximaler Geschwindigkeit gelandet 🚀",
            "hat den Server betreten wie ein Boss 😎",
            "ist ready für neue Abenteuer 🗺️",
            "bringt heute Ordnung in die Blöcke 🧱✨",
            "hat Style, Skill und einen Goldhelm 😏👑",

            // 🎲 WTF
            "hat einen Dorfbewohner bestochen 🧑‍🌾💵",
            "ist aus Versehen durch den Nether gefallen 🔥🕳️",
            "kommt aus der Zukunft zurück 🤖⏳",
            "wurde von einem Lama angespuckt 🦙💦",
            "hat gerade ein Schaf angeschrien 🐑😡",

            // 🌌 Mystisch
            "erwacht aus tiefem Minecraft-Schlaf 🌙✨",
            "hat kosmische Energie gesammelt 🌌⚡",
            "kommt mit einer Aura der Stärke zurück 🔮💫",

            // 🌿 Natur
            "kommt, um Blumen zu pflücken 🌸",
            "bringt entspannte Vibes 🌿✨",
            "hat ein paar Fische gefangen 🐟🎣",

            // 🧠 Smart
            "kommt mit neuen Konstruktionen im Kopf 🧠📐",
            "hat ein Redstone-Meisterwerk geplant 🔴⚡",

            // 🐾 Tiere
            "bringt eine Armee von Katzen mit 🐱🐱🐱",
            "hat ein neues Haustier gefunden 🐶",

            // 💸 Wirtschaft
            "verkauft heute billige Diamanten — aber nur heute 💎💸",

            // 🧱 Builder
            "hat ein neues Mega-Projekt gestartet 🧱🏗️",

            // 🧭 Abenteuer
            "hat neue Höhlen entdeckt 🕳️✨",

            // 🔥 Kampf
            "hat drei Creeper gleichzeitig besiegt 💣💚",

            // 🤖 Technik
            "hat seine Redstone-Maschine neu gestartet ⚙️🔴",

            // 💫 Prestige
            "kommt wie ein König zurück 👑",

            // 🧙 Fantasy
            "kommt als Zauberer der zweiten Stufe zurück 🧙✨"
    );


    // ⭐ Leave-Messages
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

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onJoin(PlayerJoinEvent event) {
        Player p = event.getPlayer();

        // ⭐ zufällige öffentliche Nachricht
        String text = joinMessages.get(random.nextInt(joinMessages.size()));
        event.setJoinMessage("§7✨ §e" + p.getName() + " " + text);

        // ⭐ private Box
        p.sendMessage("§8§m━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        p.sendMessage("                  §e💡 §6§lServer Info");
        p.sendMessage(" ");
        p.sendMessage("§7• §bFreunde-Menü§7 – Verwalte Freunde & Favoriten");
        p.sendMessage("§7• §aHome-System§7 – Setze Homes & teleporte sicher");
        p.sendMessage("§7• §6Erz- & Baum-System§7 – Sammle Ressourcen leichter");
        p.sendMessage("§fNutze §a/hilfe §ffür alle Systeme und Tutorials!");
        p.sendMessage("§8§m━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onQuit(PlayerQuitEvent event) {
        String text = leaveMessages.get(random.nextInt(leaveMessages.size()));
        event.setQuitMessage("§7🚪 §e" + event.getPlayer().getName() + " " + text);
    }
}
