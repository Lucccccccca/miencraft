package de.luca.plugin;

public enum HelpCategory {
    FREUNDES_SYSTEM("§b📘 Freunde-System"),
    HOME_SYSTEM("§a🏠 Home-System"),
    ERZ_BAUM_SYSTEM("§6🪵 Erz- & Baum-System"),
    SETTINGS("§d⚙ Einstellungen"),
    PREFIX_SYSTEM("§e🎨 Prefix-System"),
    CRAFTING("§6🧪 Crafting / Rezepte"),
    STATS("§b📊 Stats"),
    TELEPORT("§5🪄 Teleport / Homes"),
    ADMIN("§c🛡 Admin / Permissions"),
    SONSTIGES("§7❓ Sonstiges");

    private final String display;

    HelpCategory(String display) {
        this.display = display;
    }

    public String getDisplay() {
        return display;
    }
}
