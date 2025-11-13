package de.luca.plugin;

import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginDescriptionFile;

import java.util.Set;

public class PermissionFinder {

    /** Liest plugin.yml Permissions und trägt neue Nodes in Rollen ein */
    public static void scanAndFill(Plugin plugin) {

        PluginDescriptionFile desc = plugin.getDescription();
        Set<String> nodes = desc.getPermissions().stream()
                .map(p -> p.getName()).collect(java.util.stream.Collectors.toSet());

        RoleManager rm = ((LucaCrafterPlugin) plugin).getRoleManager();

        for (String node : nodes) {
            // Wenn unverkannt → zu "spieler" hinzufügen, damit im GUI sichtbar
            boolean existsAnywhere = rm.allRoles()
                    .stream()
                    .anyMatch(r -> rm.getRolePermissions(r).contains(node));

            if (!existsAnywhere) {
                rm.addPermissionToRole("spieler", node);
            }
        }
    }
}
