package me.ympax.neverlessspigot.commands;

import java.util.Arrays;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;

public class KillEntitiesCommand extends Command {
    public KillEntitiesCommand() {
        super("killentities");
        this.description = "Kill all entities except Players";
        this.usageMessage = "/killentities, /ke";
        this.setAliases(Arrays.asList("ke"));
        this.setPermission("neverlessspigot.killentities");
    }

    @Override
    public boolean execute(CommandSender sender, final String currentAlias, final String[] args) {
        if (!this.testPermission(sender)) {
            return true;
        }
        int i = 0;
        for (World world : Bukkit.getWorlds()) {
            for (Entity entity : world.getEntities()) {
                EntityType entityType = entity.getType();

                List<EntityType> entityTypes = Arrays.asList(
                    EntityType.DROPPED_ITEM,
                    EntityType.PRIMED_TNT,
                    EntityType.SKELETON,
                    EntityType.ZOMBIE,
                    EntityType.COW,
                    EntityType.SHEEP,
                    EntityType.ENDERMAN,
                    EntityType.PIG_ZOMBIE,
                    EntityType.PIG,
                    EntityType.CREEPER,
                    EntityType.BAT,
                    EntityType.CHICKEN,
                    EntityType.CAVE_SPIDER,
                    EntityType.SPIDER);

                if (entityTypes.contains(entityType)) {
                    entity.remove( );
                    i++;
                }
            }
        }
        sender.sendMessage(ChatColor.GREEN + "You have removed a total of " + i + " entities.");
        return false;
    }
}