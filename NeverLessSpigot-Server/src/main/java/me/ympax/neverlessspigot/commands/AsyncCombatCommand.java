package me.ympax.neverlessspigot.commands;

import java.util.Arrays;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import me.ympax.neverlessspigot.NeverLessSpigot;
import me.ympax.neverlessspigot.config.NeverLessSpigotConfig;
import net.md_5.bungee.api.ChatColor;

public class AsyncCombatCommand extends Command {
    private boolean asyncCombat = NeverLessSpigotConfig.asyncCombat;

	public AsyncCombatCommand() {
		super("asynccombat");
		this.description = "Toggles Async Combat";
		this.usageMessage = "/asynccombat, /asyncpvp";
		this.setAliases(Arrays.asList("asyncpvp"));
		this.setPermission("neverlessspigot.asynccombat");
	}

	@Override
	public boolean execute(CommandSender sender, String currentAlias, String[] args) {
		if (!testPermission(sender)) {
			return false;
		}

		asyncCombat = !asyncCombat;
		
		String status = asyncCombat ? "enabled" : "disabled";

        NeverLessSpigotConfig.asyncCombat = asyncCombat;
		NeverLessSpigotConfig.set("settings.async.combat", asyncCombat);

        if ((asyncCombat || NeverLessSpigotConfig.ticklessCombat) && (NeverLessSpigot.getInstance().getKnockbackThread() == null || !NeverLessSpigot.getInstance().getKnockbackThread().isRunning()) && (NeverLessSpigot.getInstance().getHitDetectionThread() == null || !NeverLessSpigot.getInstance().getHitDetectionThread().isRunning())) {
            NeverLessSpigot.getInstance().startAsyncThreads();
        } else if (!asyncCombat && !NeverLessSpigotConfig.ticklessCombat) {
            NeverLessSpigot.getInstance().getKnockbackThread().stop();
			NeverLessSpigot.getInstance().getHitDetectionThread().stop();
        }

		sender.sendMessage((asyncCombat ? ChatColor.GREEN : ChatColor.RED) + "Async Combat is now " + status + ".");

		return true;
	}
}
