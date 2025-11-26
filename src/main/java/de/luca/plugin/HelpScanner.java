package de.luca.plugin;

import org.bukkit.plugin.PluginDescriptionFile;

import java.util.*;

public class HelpScanner {

    public static Map<HelpCategory, List<String>> scan(LucaCrafterPlugin plugin) {

        Map<HelpCategory, List<String>> out = new HashMap<>();
        for (HelpCategory c : HelpCategory.values()) {
            out.put(c, new ArrayList<>());
        }

        PluginDescriptionFile pdf = plugin.getDescription();

        Map<String, Map<String, Object>> commands = pdf.getCommands();
        if (commands == null) return out;

        for (String cmd : commands.keySet()) {

            HelpCategory category = categorize(cmd);

            out.get(category).add(cmd);
        }

        return out;
    }

    private static HelpCategory categorize(String cmd) {
        cmd = cmd.toLowerCase();

        if (cmd.startsWith("freund") || cmd.startsWith("freunde")) return HelpCategory.FREUNDES_SYSTEM;
        if (cmd.startsWith("home") || cmd.startsWith("sethome") || cmd.startsWith("delhome")) return HelpCategory.HOME_SYSTEM;
        if (cmd.equals("erz") || cmd.equals("baum")) return HelpCategory.ERZ_BAUM_SYSTEM;
        if (cmd.equals("settings")) return HelpCategory.SETTINGS;
        if (cmd.startsWith("prefix")) return HelpCategory.PREFIX_SYSTEM;
        if (cmd.startsWith("craft")) return HelpCategory.CRAFTING;
        if (cmd.equals("stats")) return HelpCategory.STATS;
        if (cmd.startsWith("tp") || cmd.startsWith("warp")) return HelpCategory.TELEPORT;
        if (cmd.startsWith("perm") || cmd.startsWith("perms")) return HelpCategory.ADMIN;

        return HelpCategory.SONSTIGES;
    }
}
