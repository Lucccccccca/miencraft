package de.luca.plugin;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.plugin.java.JavaPlugin;

public final class LucaCrafterPlugin extends JavaPlugin {

    private static LucaCrafterPlugin instance;

    // ======= Managers ========
    private ConfigManager configManager;
    private RoleManager roleManager;
    private RecipeStorage recipeStorage;
    private PermissionUpdater permissionUpdater;

    // PREFIX-SYSTEM
    private PlayerPrefixManager playerPrefixManager;
    private PrefixUpdater prefixUpdater;

    // HOME-SYSTEM
    private HomeManager homeManager;
    private HomeMarkerTask homeMarkerTask;

    // ======= GETTERS ========
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

    public PlayerPrefixManager getPlayerPrefixManager() {
        return playerPrefixManager;
    }

    public PrefixUpdater getPrefixUpdater() {
        return prefixUpdater;
    }

    public HomeManager getHomeManager() {
        return homeManager;
    }

    @Override
    public void onEnable() {

        instance = this;

        // Datenordner
        if (!getDataFolder().exists()) {
            getDataFolder().mkdirs();
        }

        // ===============================
        //  MANAGER LADEN
        // ===============================
        configManager = new ConfigManager(this);
        roleManager = new RoleManager(this);

        // PREFIX-MANAGER (muss vor PrefixUpdater kommen!)
        playerPrefixManager = new PlayerPrefixManager(this);
        prefixUpdater = new PrefixUpdater(this, roleManager, playerPrefixManager);

        // PERMISSION-UPDATER
        permissionUpdater = new PermissionUpdater(this);
        Bukkit.getPluginManager().registerEvents(permissionUpdater, this);

        // ===============================
        //  HOME-MANAGER + PARTIKEL-TASK
        // ===============================
        homeManager = new HomeManager(this);

        // Zeichnet ggf. Partikelringe um Homes
        homeMarkerTask = new HomeMarkerTask(this, homeManager);
        // alle 2 Sekunden
        homeMarkerTask.runTaskTimer(this, 40L, 40L);

        // ===============================
        //  PREFIX-SYSTEM COMMANDS & LISTENER
        // ===============================
        if (getCommand("prefix") != null) {
            getCommand("prefix").setExecutor(new PrefixCommand(this));
        }

        PrefixChatListener prefixChatListener = new PrefixChatListener(this);
        Bukkit.getPluginManager().registerEvents(
                new PrefixGUIListener(this, prefixChatListener), this);
        Bukkit.getPluginManager().registerEvents(prefixChatListener, this);

        // ===============================
        //  HOME-COMMANDS & LISTENER
        // ===============================
        if (getCommand("home") != null) {
            getCommand("home").setExecutor(new HomeCommand(this));
        }
        if (getCommand("sethome") != null) {
            getCommand("sethome").setExecutor(new SetHomeCommand(this));
        }
        if (getCommand("delhome") != null) {
            getCommand("delhome").setExecutor(new DelHomeCommand(this));
        }
        if (getCommand("homesadmin") != null) {
            getCommand("homesadmin").setExecutor(new HomesAdminCommand(this));
        }

        // GUI + Teleport-Handling für Homes
        Bukkit.getPluginManager().registerEvents(new HomeGUIListener(this), this);
        Bukkit.getPluginManager().registerEvents(new HomeTeleportHandler(this), this);

        // ===============================
        //  BAUM-SYSTEM
        // ===============================
        BaumCommand baumCommand = new BaumCommand(this);
        if (getCommand("baum") != null) {
            getCommand("baum").setExecutor(baumCommand);
        }
        Bukkit.getPluginManager().registerEvents(baumCommand, this);
        Bukkit.getPluginManager().registerEvents(new BaumListener(this, baumCommand), this);

        // ===============================
        //  ERZ-SYSTEM
        // ===============================
        ErzCommand erzCommand = new ErzCommand(this);
        if (getCommand("erz") != null) {
            getCommand("erz").setExecutor(erzCommand);
        }
        Bukkit.getPluginManager().registerEvents(erzCommand, this);
        Bukkit.getPluginManager().registerEvents(new ErzListener(this, erzCommand), this);

        // ===============================
        //  STATS-SYSTEM
        // ===============================
        StatsCommand statsCommand = new StatsCommand(this);
        if (getCommand("stats") != null) {
            getCommand("stats").setExecutor(statsCommand);
        }
        Bukkit.getPluginManager().registerEvents(statsCommand, this);

        // ===============================
        //  FARM / ANTI-CREEPER
        // ===============================
        Bukkit.getPluginManager().registerEvents(new FarmProtectListener(this), this);
        Bukkit.getPluginManager().registerEvents(new AntiCreeperListener(this), this);

        // ===============================
        //  SETTINGS
        // ===============================
        if (getCommand("settings") != null) {
            getCommand("settings").setExecutor(new SettingsCommand(this));
        }
        Bukkit.getPluginManager().registerEvents(new SettingsGUIListener(this), this);

        // ===============================
        //  SERVER-SETTINGS
        // ===============================
        if (getCommand("server") != null) {
            getCommand("server").setExecutor(new ServerSettingsCommand(this));
        }
        Bukkit.getPluginManager().registerEvents(new ServerSettingsListener(this), this);

        // ===============================
        //  CUSTOM CRAFTING
        // ===============================
        recipeStorage = new RecipeStorage(this);
        if (getCommand("craftgui") != null) {
            getCommand("craftgui").setExecutor(new CraftGUICommand(this));
        }
        Bukkit.getPluginManager().registerEvents(new CraftGUIListener(this), this);

        // ===============================
        //  FAST FURNACE
        // ===============================
        Bukkit.getPluginManager().registerEvents(new FastFurnaceListener(this), this);

        // ===============================
        //  MAGNET / AUTOPICKUP
        // ===============================
        Bukkit.getPluginManager().registerEvents(new MagnetListener(this), this);
        Bukkit.getPluginManager().registerEvents(new AutoPickupListener(this), this);

        // ===============================
        //  ALWAYS DROP
        // ===============================
        Bukkit.getPluginManager().registerEvents(new AlwaysDropListener(this), this);

        // ===============================
        //  AFK-SYSTEM
        // ===============================
        AfkCommand afkCommand = new AfkCommand(this);
        if (getCommand("afk") != null) {
            getCommand("afk").setExecutor(afkCommand);
        }
        Bukkit.getPluginManager().registerEvents(afkCommand, this);

        // ===============================
        //  RESTART-SYSTEM
        // ===============================
        if (getCommand("restartserver") != null) {
            getCommand("restartserver").setExecutor(new RestartServerCommand(this));
        }

        // ===============================
        //  PERMS-GUI / INSPECTOR
        // ===============================
        PermsCommand permsCommand = new PermsCommand(this);
        if (getCommand("perms") != null) {
            getCommand("perms").setExecutor(permsCommand);
        }
        Bukkit.getPluginManager().registerEvents(new PermsGUIListener(this), this);

        if (getCommand("permcheck") != null) {
            getCommand("permcheck").setExecutor(new PermissionInspectorCommand());
        }
        if (getCommand("permsreload") != null) {
            getCommand("permsreload").setExecutor(new PermsReloadCommand(this));
        }

        // scannt alle Permission-Nodes
        PermissionFinder.scanAndFill(this);

        // ===============================
        //  SPAWN-EGG-RECIPES
        // ===============================
        SpawnEggRecipeManager spawnEggs = new SpawnEggRecipeManager(this);
        spawnEggs.registerAllSpawnEggs();
        Bukkit.getPluginManager().registerEvents(new SpawnEggCraftListener(this), this);

        // ===============================
        //  JOIN / LEAVE
        // ===============================
        Bukkit.getPluginManager().registerEvents(new JoinListener(), this);

        // ===============================
        //  STÜNDLICHER TIPP
        // ===============================
        long hour = 20L * 60L * 60L;
        Bukkit.getScheduler().runTaskTimer(this, () -> {
            Bukkit.broadcastMessage(ChatColor.YELLOW + "💡 Tipp: Nutze "
                    + ChatColor.GREEN + "/baum" + ChatColor.YELLOW + ", "
                    + ChatColor.AQUA + "/erz" + ChatColor.YELLOW + ", "
                    + ChatColor.GOLD + "/settings" + ChatColor.YELLOW + "!");
        }, hour, hour);

        getLogger().info("✅ LucaCrafterPlugin erfolgreich aktiviert!");
    }

    @Override
    public void onDisable() {
        getLogger().info("❌ LucaCrafterPlugin wurde deaktiviert!");
    }
}
