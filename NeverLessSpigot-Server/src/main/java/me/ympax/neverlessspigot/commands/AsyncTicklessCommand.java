package me.ympax.neverlessspigot.commands;

import java.util.Arrays;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import me.ympax.neverlessspigot.NeverLessSpigot;
import me.ympax.neverlessspigot.config.NeverLessSpigotConfig;

public class AsyncTicklessCommand extends Command {
    private boolean asyncTickless = NeverLessSpigotConfig.asyncTickless;

	public AsyncTicklessCommand() {
		super("asynctickless");
		this.description = "Toggles Async Combat";
		this.usageMessage = ChatColor.RED.toString() + "/asynctickless";
		this.setAliases(Arrays.asList("ticklesspvp"));
		this.setPermission("neverlessspigot.ticklesscombat");
	}

	@Override
	public boolean execute(CommandSender sender, String currentAlias, String[] args) {
		if (!testPermission(sender)) {
			return true;
		}

		asyncTickless = !asyncTickless;
		
		String status = asyncTickless ? "enabled" : "disabled";

        NeverLessSpigotConfig.asyncTickless = asyncTickless;
		NeverLessSpigotConfig.set("settings.tickless.async", asyncTickless);

		sender.sendMessage((asyncTickless ? ChatColor.GREEN : ChatColor.RED) + "Async Tickless is now " + status + ".");

		if (asyncTickless) {
			if ((NeverLessSpigotConfig.ticklessCombat || NeverLessSpigotConfig.asyncCombat) && (NeverLessSpigot.getInstance().getKnockbackThread() == null || !NeverLessSpigot.getInstance().getKnockbackThread().isRunning()) && (NeverLessSpigot.getInstance().getHitDetectionThread() == null || !NeverLessSpigot.getInstance().getHitDetectionThread().isRunning())) {
				NeverLessSpigot.getInstance().startAsyncThreads();
			}
		} else {
			if (NeverLessSpigot.getInstance().getKnockbackThread() != null && NeverLessSpigot.getInstance().getKnockbackThread().isRunning()) {
				NeverLessSpigot.getInstance().getKnockbackThread().stop();
			}

			if (NeverLessSpigot.getInstance().getHitDetectionThread() != null && NeverLessSpigot.getInstance().getHitDetectionThread().isRunning()) {
				NeverLessSpigot.getInstance().getHitDetectionThread().stop();
			}
		}

		return true;
	}
}
