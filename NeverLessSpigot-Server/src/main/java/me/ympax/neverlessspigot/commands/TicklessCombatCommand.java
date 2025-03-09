package me.ympax.neverlessspigot.commands;

import java.util.Arrays;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import me.ympax.neverlessspigot.NeverLessSpigot;
import me.ympax.neverlessspigot.config.NeverLessSpigotConfig;
import net.md_5.bungee.api.ChatColor;

public class TicklessCombatCommand extends Command {
    private boolean ticklessCombat = NeverLessSpigotConfig.ticklessCombat;

	public TicklessCombatCommand() {
		super("ticklesscombat");
		this.description = "Toggles Async Combat";
		this.usageMessage = "/ticklesscombat, /ticklesspvp";
		this.setAliases(Arrays.asList("ticklesspvp"));
		this.setPermission("neverlessspigot.ticklesscombat");
	}

	@Override
	public boolean execute(CommandSender sender, String currentAlias, String[] args) {
		if (!testPermission(sender)) {
			return true;
		}

		ticklessCombat = !ticklessCombat;
		
		String status = ticklessCombat ? "enabled" : "disabled";

        NeverLessSpigotConfig.ticklessCombat = ticklessCombat;
		NeverLessSpigotConfig.set("settings.tickless.combat", ticklessCombat);

        if ((ticklessCombat || NeverLessSpigotConfig.asyncCombat) && (NeverLessSpigot.getInstance().getKnockbackThread() == null || !NeverLessSpigot.getInstance().getKnockbackThread().isRunning()) && (NeverLessSpigot.getInstance().getHitDetectionThread() == null || !NeverLessSpigot.getInstance().getHitDetectionThread().isRunning())) {
            NeverLessSpigot.getInstance().startAsyncThreads();
        } else if (!ticklessCombat && !NeverLessSpigotConfig.asyncCombat) {
            NeverLessSpigot.getInstance().getKnockbackThread().stop();
			NeverLessSpigot.getInstance().getHitDetectionThread().stop();
        }

		sender.sendMessage((ticklessCombat ? ChatColor.GREEN : ChatColor.RED) + "Tickless Combat is now " + status + ".");

		return true;
	}
}
