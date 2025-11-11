package de.luca.plugin;

import org.bukkit.Bukkit;
import org.bukkit.Server;
import org.bukkit.plugin.java.JavaPlugin;

public final class LucaCrafterPlugin extends JavaPlugin {

    private static LucaCrafterPlugin instance;
    private ConfigManager configManager;
    private RoleManager roleManager;
    private RecipeStorage recipeStorage;

    // === Getter ===
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

    // === onEnable ===
    @Override
    public void onEnable() {
        instance = this;

        // 📁 Plugin-Ordner erstellen
        if (!getDataFolder().exists()) getDataFolder().mkdirs();

        getLogger().info("📦 Lade Systeme...");

        // 🧩 Manager initialisieren
        configManager = new ConfigManager(this);
        roleManager = new RoleManager(this);
        getLogger().info("✅ ConfigManager & RoleManager geladen.");

        // ===============================
        // 🌲 Baum-System
        // ===============================
        BaumCommand baumCommand = new BaumCommand(this);
        getCommand("baum").setExecutor(baumCommand);
        Bukkit.getPluginManager().registerEvents(baumCommand, this);
        Bukkit.getPluginManager().registerEvents(new BaumListener(this, baumCommand), this);

        // ===============================
        // ⛏️ Erz-System
        // ===============================
        ErzCommand erzCommand = new ErzCommand(this);
        getCommand("erz").setExecutor(erzCommand);
        Bukkit.getPluginManager().registerEvents(erzCommand, this);
        Bukkit.getPluginManager().registerEvents(new ErzListener(this, erzCommand), this);

        // ===============================
        // 📊 Statistik-System
        // ===============================
        StatsCommand statsCommand = new StatsCommand(this);
        getCommand("stats").setExecutor(statsCommand);
        Bukkit.getPluginManager().registerEvents(statsCommand, this);

        // ===============================
        // 🧱 Farm & Anti-Creeper
        // ===============================
        Bukkit.getPluginManager().registerEvents(new FarmProtectListener(this), this);
        Bukkit.getPluginManager().registerEvents(new AntiCreeperListener(this), this);
        getLogger().info("✅ Farm-Protection & Anti-Creeper aktiviert!");

        // ===============================
        // ⚙️ Settings-System
        // ===============================
        getCommand("settings").setExecutor(new SettingsCommand(this));
        Bukkit.getPluginManager().registerEvents(new SettingsGUIListener(this), this);

        getLogger().info("✅ Neues Settings-System aktiviert!");




        //Server Settings
        getCommand("server").setExecutor(new ServerSettingsCommand(this));
        Bukkit.getPluginManager().registerEvents(new ServerSettingsListener(this), this);
        getLogger().info("✅ Server Settings-System aktiviert!");

        // ===============================
        // ⚡ Fast Furnace
        // ===============================
        Bukkit.getPluginManager().registerEvents(new FastFurnaceListener(this), this);
        getLogger().info("✅ Fast Furnace aktiviert!");

        // ===============================
        // 🧲 Magnet & AutoPickup
        // ===============================
        Bukkit.getPluginManager().registerEvents(new MagnetListener(this), this);
        Bukkit.getPluginManager().registerEvents(new AutoPickupListener(this), this);
        getLogger().info("✅ Magnet- & AutoPickup-System aktiviert!");

        // ===============================
        // 🧱 AlwaysDrop (zerstörbare Blöcke bleiben erhalten)
        // ===============================
        Bukkit.getPluginManager().registerEvents(new AlwaysDropListener(this), this);
        getLogger().info("✅ AlwaysDrop aktiviert!");

        // ===============================
        // 🧩 Custom Crafting-System
        // ===============================
        recipeStorage = new RecipeStorage(this);
        getCommand("craftgui").setExecutor(new CraftGUICommand(this));
        Bukkit.getPluginManager().registerEvents(new CraftGUIListener(this), this);
        getLogger().info("✅ Custom Craft GUI & Recipes geladen!");

        // ===============================
        // 💤 AFK-System
        // ===============================
        AfkCommand afkCommand = new AfkCommand(this);
        getCommand("afk").setExecutor(afkCommand);
        Bukkit.getPluginManager().registerEvents(afkCommand, this);
        getLogger().info("✅ AFK-System aktiviert!");

        // ===============================
        // 🧑‍💼 Permissions & Rollen-System
        // ===============================
        PermsCommand permsCommand = new PermsCommand(this);
        getCommand("perms").setExecutor(permsCommand);
        Bukkit.getPluginManager().registerEvents(new PermsGUIListener(this), this);
        getLogger().info("✅ Permissions- & Rollen-System aktiviert!");

        // ===============================
        // 🥚 Spawn-Egg-Rezepte
        // ===============================
        SpawnEggRecipeManager recipeManager = new SpawnEggRecipeManager(this);
        recipeManager.registerAllSpawnEggs();
        Bukkit.getPluginManager().registerEvents(new SpawnEggCraftListener(this), this);
        getLogger().info("✅ Alle Spawn-Egg-Rezepte aktiviert!");

        // ===============================
        // 👋 Join/Leave-Events
        // ===============================
        Bukkit.getPluginManager().registerEvents(new JoinListener(), this);

        getLogger().info("✅ LucaCrafterPlugin erfolgreich aktiviert!");
    }

    // === onDisable ===
    @Override
    public void onDisable() {
        getLogger().info("❌ LucaCrafterPlugin wurde deaktiviert!");
    }
}
