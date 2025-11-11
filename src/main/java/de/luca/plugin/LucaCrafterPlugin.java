package de.luca.plugin;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * Hauptklasse des Plugins. Registriert alle Systeme, inkl. PermissionUpdater und stündlicher Tipp-Nachricht.
 */
public final class LucaCrafterPlugin extends JavaPlugin {

    private static LucaCrafterPlugin instance;
    private ConfigManager configManager;
    private RoleManager roleManager;
    private RecipeStorage recipeStorage;
    private PermissionUpdater permissionUpdater;

    public static LucaCrafterPlugin getInstance() {
        return instance;
    }

    public ConfigManager getConfigManager() {
        return configManager;
    }

    public RoleManager getRoleManager() {
        return roleManager;
    }

    public RecipeStorage getRecipeStorage() {
        return recipeStorage;
    }

    public PermissionUpdater getPermissionUpdater() {
        return permissionUpdater;
    }

    @Override
    public void onEnable() {
        instance = this;

        // 📁 Ordner erstellen
        if (!getDataFolder().exists()) getDataFolder().mkdirs();

        // 🧩 Manager
        configManager = new ConfigManager(this);
        roleManager = new RoleManager(this);

        // 🆕 PermissionUpdater registrieren
        permissionUpdater = new PermissionUpdater(this);
        Bukkit.getPluginManager().registerEvents(permissionUpdater, this);

        // 🌲 Baum-System
        BaumCommand baumCommand = new BaumCommand(this);
        getCommand("baum").setExecutor(baumCommand);
        Bukkit.getPluginManager().registerEvents(baumCommand, this);
        Bukkit.getPluginManager().registerEvents(new BaumListener(this, baumCommand), this);

        // ⛏ Erz-System
        ErzCommand erzCommand = new ErzCommand(this);
        getCommand("erz").setExecutor(erzCommand);
        Bukkit.getPluginManager().registerEvents(erzCommand, this);
        Bukkit.getPluginManager().registerEvents(new ErzListener(this, erzCommand), this);

        // 📊 Stats
        StatsCommand statsCommand = new StatsCommand(this);
        getCommand("stats").setExecutor(statsCommand);
        Bukkit.getPluginManager().registerEvents(statsCommand, this);

        // 🧱 Farm & Anti-Creeper
        Bukkit.getPluginManager().registerEvents(new FarmProtectListener(this), this);
        Bukkit.getPluginManager().registerEvents(new AntiCreeperListener(this), this);

        // ⚙ Settings
        getCommand("settings").setExecutor(new SettingsCommand(this));
        Bukkit.getPluginManager().registerEvents(new SettingsGUIListener(this), this);

        // 🧑‍💼 Server-Einstellungen
        getCommand("server").setExecutor(new ServerSettingsCommand(this));
        Bukkit.getPluginManager().registerEvents(new ServerSettingsListener(this), this);

        // ⚡ Fast Furnace
        Bukkit.getPluginManager().registerEvents(new FastFurnaceListener(this), this);

        // 🧲 Magnet & AutoPickup
        Bukkit.getPluginManager().registerEvents(new MagnetListener(this), this);
        Bukkit.getPluginManager().registerEvents(new AutoPickupListener(this), this);

        // 🧱 AlwaysDrop
        Bukkit.getPluginManager().registerEvents(new AlwaysDropListener(this), this);

        // 🧩 Custom Crafting
        recipeStorage = new RecipeStorage(this);
        getCommand("craftgui").setExecutor(new CraftGUICommand(this));
        Bukkit.getPluginManager().registerEvents(new CraftGUIListener(this), this);

        // 💤 AFK
        AfkCommand afkCommand = new AfkCommand(this);
        getCommand("afk").setExecutor(afkCommand);
        Bukkit.getPluginManager().registerEvents(afkCommand, this);


try {
    // Zugriff auf die interne CommandMap
    org.bukkit.command.SimpleCommandMap commandMap = (org.bukkit.command.SimpleCommandMap)
            Bukkit.getServer().getClass().getMethod("getCommandMap").invoke(Bukkit.getServer());

    // Alten /restart-Befehl entfernen
    org.bukkit.command.Command oldRestart = commandMap.getCommand("restart");
    if (oldRestart != null) {
        oldRestart.unregister(commandMap);
        getLogger().info("🧹 Alter Spigot-/restart-Command erfolgreich entfernt.");
    }

    // Deinen eigenen RestartCommand direkt registrieren
    org.bukkit.command.Command newRestart = new RestartCommand(this);
    commandMap.register("LucaCrafterPlugin", newRestart);
    getLogger().info("⚡ Neuer /restart-Command vom LucaCrafterPlugin wurde registriert.");
} catch (Exception e) {
    getLogger().warning("⚠️ Konnte /restart nicht neu registrieren: " + e.getMessage());
}


        // 🧑‍💼 Perms
        PermsCommand permsCommand = new PermsCommand(this);
        getCommand("perms").setExecutor(permsCommand);
        Bukkit.getPluginManager().registerEvents(new PermsGUIListener(this), this);

        // 🥚 Spawn-Eggs
        SpawnEggRecipeManager recipeManager = new SpawnEggRecipeManager(this);
        recipeManager.registerAllSpawnEggs();
        Bukkit.getPluginManager().registerEvents(new SpawnEggCraftListener(this), this);

        // 👋 Join/Leave
        Bukkit.getPluginManager().registerEvents(new JoinListener(), this);

        // 🕒 Tipp-Nachricht alle Stunde
        long stunde = 20L * 60 * 60;
        Bukkit.getScheduler().runTaskTimer(this, () -> {
            String msg = ChatColor.YELLOW + "💡 Tipp: Nutze "
                    + ChatColor.GREEN + "/baum" + ChatColor.YELLOW + " für den Baumfäller, "
                    + ChatColor.AQUA + "/erz" + ChatColor.YELLOW + " für das Erz-System "
                    + "und " + ChatColor.GOLD + "/settings" + ChatColor.YELLOW + " für deine Einstellungen!";
            Bukkit.broadcastMessage(msg);
        }, stunde, stunde);

        getLogger().info("✅ LucaCrafterPlugin erfolgreich aktiviert!");
    }

    @Override
    public void onDisable() {
        getLogger().info("❌ LucaCrafterPlugin wurde deaktiviert!");
    }
}
