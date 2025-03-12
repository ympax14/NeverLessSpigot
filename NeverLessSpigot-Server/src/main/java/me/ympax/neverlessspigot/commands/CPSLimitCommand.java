package me.ympax.neverlessspigot.commands;

import org.apache.commons.lang3.math.NumberUtils;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import me.ympax.neverlessspigot.config.NeverLessSpigotConfig;

public class CPSLimitCommand extends Command {
    public CPSLimitCommand() {
        super("cpslimit");
        this.usageMessage = ChatColor.RED.toString() + "Please use /cpslimit <cps>";
        this.setPermission("neverlessspigot.cpslimit");
    }

    public boolean execute(CommandSender sender, String commandLabel, String[] args) {
		if (!testPermission(sender)) return false;

        if (args.length == 0) {
            sender.sendMessage(ChatColor.DARK_PURPLE.toString() + "The current CPS limit is: " + ChatColor.YELLOW.toString() + NeverLessSpigotConfig.cpsLimit + "CPS");
        } else {
            if (!NumberUtils.isNumber(args[0])) {
                sender.sendMessage(ChatColor.RED.toString() + "Invalid CPS limit.");
                return true;
            }

            int cpsLimit = Integer.parseInt(args[0]);

            if (cpsLimit < 0) {
                sender.sendMessage(ChatColor.RED.toString() + "Invalid CPS limit. Must be above 0.");
                return true;
            }

            NeverLessSpigotConfig.cpsLimit = cpsLimit;
		    NeverLessSpigotConfig.set("settings.cps-limit", cpsLimit);

            sender.sendMessage(ChatColor.GREEN.toString() + "New CPS limit: " + ChatColor.YELLOW.toString() + NeverLessSpigotConfig.cpsLimit + "CPS");
        }

        return true;
    }
}
