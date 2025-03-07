package me.ympax.neverlessspigot.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import me.ympax.neverlessspigot.NeverLessSpigot;
import me.ympax.neverlessspigot.config.NeverLessSpigotConfig;

public class CombatTPSCommand extends Command {
    public CombatTPSCommand() {
        super("combattps");
        this.usageMessage = ChatColor.RED.toString() + "Please use /combattps <tps>";
        this.setPermission("neverlessspigot.combattps");
    }

    public boolean execute(CommandSender sender, String commandLabel, String[] args) {
		if (!testPermission(sender)) return false;
		
		if (args.length != 1) {
			sender.sendMessage(this.usageMessage);
			return false;
		}
        
		try {
			if (Integer.parseInt(args[0]) <= 0) {
				sender.sendMessage(ChatColor.RED + "TPS must be higher than 0.");
				return false;
			}

			int tps = Integer.parseInt(args[0]);

			NeverLessSpigotConfig.combatThreadTPS = tps;
			NeverLessSpigotConfig.set("settings.async.combat-thread-tps", tps);

			if (NeverLessSpigot.getInstance().getKnockbackThread() != null && NeverLessSpigot.getInstance().getKnockbackThread().isRunning()) {
				NeverLessSpigot.getInstance().getKnockbackThread().setTPS(tps);
			}

			if (NeverLessSpigot.getInstance().getHitDetectionThread() != null && NeverLessSpigot.getInstance().getHitDetectionThread().isRunning()) {
				NeverLessSpigot.getInstance().getHitDetectionThread().setTPS(tps);
			}

			sender.sendMessage(ChatColor.GREEN + "Players TPS is now set at " + ChatColor.GOLD + tps + ChatColor.GREEN + ".");
			return true;
		} catch (Exception ex) {
			sender.sendMessage(ChatColor.RED + "Please enter a number instead of '" + args[0] + "'.");
			return false;
		}
	}
}