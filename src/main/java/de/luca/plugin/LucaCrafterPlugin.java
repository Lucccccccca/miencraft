package de.luca.plugin;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.plugin.PluginManager;
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
    private HomeTeleportLogic homeTeleportLogic;
    private HomeTeleportHandler homeTeleportHandler;
    private HomeHologramManager homeHologramManager;

    // FREUNDES-SYSTEM
    private FriendManager friendManager;
    private ManualNameInput manualNameInput;

    // ======= GETTERS ========

    public ManualNameInput getManualNameInput() { return manualNameInput; }

    public FriendManager getFriendManager() { return friendManager; }

    public HomeHologramManager getHomeHologramManager() { return homeHologramManager; }

    public static LucaCrafterPlugin getInstance() { return instance; }

    public ConfigManager getConfigManager() { return configManager; }

    public RoleManager getRoleManager() { return roleManager; }

    public RecipeStorage getRecipeStorage() { return recipeStorage; }

    public PermissionUpdater getPermissionUpdater() { return permissionUpdater; }

    public PlayerPrefixManager getPlayerPrefixManager() { return playerPrefixManager; }

    public PrefixUpdater getPrefixUpdater() { return prefixUpdater; }

    public HomeManager getHomeManager() { return homeManager; }

    public HomeTeleportLogic getHomeTeleportLogic() { return homeTeleportLogic; }

    public HomeTeleportHandler getHomeTeleportHandler() { return homeTeleportHandler; }

    @Override
    public void onEnable() {

        instance = this;

        // Datenordner
        if (!getDataFolder().exists()) {
            getDataFolder().mkdirs();
        }

        PluginManager pm = Bukkit.getPluginManager();

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
        pm.registerEvents(permissionUpdater, this);

        // ===============================
        //  FREUNDES-SYSTEM LADEN
        // ===============================
        friendManager = new FriendManager(this);

        getCommand("freunde").setExecutor(new FreundeCommand(this));
        getCommand("freunde").setTabCompleter(new FreundeCommand(this));

        // GUI Listener
        pm.registerEvents(new FreundeGUIListener(this), this);
        pm.registerEvents(new FriendMainListener(this), this);
        pm.registerEvents(new FreundeJoinListener(this), this);

        // Chat-Eingabe (manuelle Namenseingabe)
        manualNameInput = new ManualNameInput(this);
        pm.registerEvents(manualNameInput, this);

        // Aliases
        if (getCommand("friend") != null)
            getCommand("friend").setExecutor(new FreundeCommand(this));
        if (getCommand("friends") != null)
            getCommand("friends").setExecutor(new FreundeCommand(this));

        // ===============================
        //  HOME-SYSTEM
        // ===============================
        homeManager = new HomeManager(this);
        homeTeleportLogic = new HomeTeleportLogic(this);
        homeTeleportHandler = new HomeTeleportHandler(this);

        // Partikelringe um Homes
        homeMarkerTask = new HomeMarkerTask(this, homeManager);
        homeMarkerTask.runTaskTimer(this, 40L, 40L);

        // HOME GUI + Listener
        pm.registerEvents(new HomeGUIListener(this, homeTeleportHandler), this);
        pm.registerEvents(homeTeleportHandler, this);
        pm.registerEvents(new HomeAdminSettingsListener(this), this);

        if (getCommand("home") != null)
            getCommand("home").setExecutor(new HomeCommand(this));
        if (getCommand("sethome") != null)
            getCommand("sethome").setExecutor(new SetHomeCommand(this));
        if (getCommand("delhome") != null)
            getCommand("delhome").setExecutor(new DelHomeCommand(this));
        if (getCommand("homesadmin") != null)
            getCommand("homesadmin").setExecutor(new HomesAdminCommand(this));

        // Homes verbunden mit Freundes-System
        pm.registerEvents(new FriendGUIListener(this), this);
        pm.registerEvents(new FriendHomesGUIListener(this, homeTeleportHandler), this);

        // ===============================
        //  HOLOGRAMME FÜR HOMES
        // ===============================
        homeHologramManager = new HomeHologramManager(this);
        pm.registerEvents(new HomeHologramJoinListener(this), this);

        // ===============================
        //  BAUM-SYSTEM
        // ===============================
        BaumCommand baumCommand = new BaumCommand(this);
        if (getCommand("baum") != null)
            getCommand("baum").setExecutor(baumCommand);
        pm.registerEvents(baumCommand, this);
        pm.registerEvents(new BaumListener(this, baumCommand), this);

        // ===============================
        //  ERZ-SYSTEM
        // ===============================
        ErzCommand erzCommand = new ErzCommand(this);
        if (getCommand("erz") != null)
            getCommand("erz").setExecutor(erzCommand);
        pm.registerEvents(erzCommand, this);
        pm.registerEvents(new ErzListener(this, erzCommand), this);

        // ===============================
        //  STATS
        // ===============================
        StatsCommand statsCommand = new StatsCommand(this);
        if (getCommand("stats") != null)
            getCommand("stats").setExecutor(statsCommand);
        pm.registerEvents(statsCommand, this);

        // ===============================
        //  FARM / ANTI-CREEPER
        // ===============================
        pm.registerEvents(new FarmProtectListener(this), this);
        pm.registerEvents(new AntiCreeperListener(this), this);

        // ===============================
        //  FURNACE / MAGNET / AUTOPICKUP
        // ===============================
        pm.registerEvents(new FastFurnaceListener(this), this);
        pm.registerEvents(new MagnetListener(this), this);
        pm.registerEvents(new AutoPickupListener(this), this);

        // ===============================
        //  ALWAYS DROP
        // ===============================
        pm.registerEvents(new AlwaysDropListener(this), this);

        // ===============================
        //  AFK
        // ===============================
        AfkCommand afkCommand = new AfkCommand(this);
        if (getCommand("afk") != null)
            getCommand("afk").setExecutor(afkCommand);
        pm.registerEvents(afkCommand, this);

        // ===============================
        //  RESTART
        // ===============================
        if (getCommand("restartserver") != null)
            getCommand("restartserver").setExecutor(new RestartServerCommand(this));

        // ===============================
        //  SERVER SETTINGS
        // ===============================
        if (getCommand("settings") != null)
            getCommand("settings").setExecutor(new SettingsCommand(this));
        pm.registerEvents(new SettingsGUIListener(this), this);
        pm.registerEvents(new PrefixJoinListener(this), this);



        if (getCommand("server") != null)
            getCommand("server").setExecutor(new ServerSettingsCommand(this));
        pm.registerEvents(new ServerSettingsListener(this), this);

        pm.registerEvents(new PluginInfoGUIListener(this), this);

        getCommand("hilfe").setExecutor(new HilfeCommand(this));
        pm.registerEvents(new HelpGUIListener(this), this);

        // ===============================
        //  PERMISSIONS SYSTEM
        // ===============================
        PermsCommand permsCommand = new PermsCommand(this);
        if (getCommand("perms") != null)
            getCommand("perms").setExecutor(permsCommand);

        pm.registerEvents(new PermsGUIListener(this), this);

        if (getCommand("permcheck") != null)
            getCommand("permcheck").setExecutor(new PermissionInspectorCommand());
        if (getCommand("permsreload") != null)
            getCommand("permsreload").setExecutor(new PermsReloadCommand(this));

        PermissionFinder.scanAndFill(this);

        // ===============================
        //  CUSTOM CRAFTING
        // ===============================
        recipeStorage = new RecipeStorage(this);

        if (getCommand("craftgui") != null)
            getCommand("craftgui").setExecutor(new CraftGUICommand(this));

        pm.registerEvents(new CraftGUIListener(this), this);

        // ===============================
        //  SPAWN-EGG RECIPES
        // ===============================
        SpawnEggRecipeManager spawnEggs = new SpawnEggRecipeManager(this);
        spawnEggs.registerAllSpawnEggs();
        pm.registerEvents(new SpawnEggCraftListener(this), this);

        // ===============================
        //  JOIN / LEAVE
        // ===============================
        pm.registerEvents(new JoinListener(), this);

        // ===============================
        //  STÜNDLICHER TIPP
        // ===============================
long hour = 20L * 60L * 60L; // 1 Stunde

Bukkit.getScheduler().runTaskTimer(this, () -> {

    Bukkit.broadcastMessage(" ");
    Bukkit.broadcastMessage("§8§m━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
    Bukkit.broadcastMessage("      §e💡 §6§lTIPP DES TAGES");
    Bukkit.broadcastMessage(" ");
    Bukkit.broadcastMessage("§7• §bFreunde-Menü§7 – Verwalte Freunde & Favoriten");
    Bukkit.broadcastMessage("§7• §aHome-System§7 – Setze Homes & teleporte sicher");
    Bukkit.broadcastMessage("§7• §6Erz- & Baum-System§7 – Sammle Ressourcen leichter");
    Bukkit.broadcastMessage(" ");
    Bukkit.broadcastMessage("§fNutze §a/hilfe §ffür alle Systeme und Tutorials!");
    Bukkit.broadcastMessage("§8§m━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
    Bukkit.broadcastMessage(" ");

}, hour, hour);

        getLogger().info("✅ LucaCrafterPlugin erfolgreich aktiviert!");
    }

    @Override
    public void onDisable() {
        getLogger().info("❌ LucaCrafterPlugin wurde deaktiviert!");
    }
}
