package me.ympax.neverlessspigot.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import me.ympax.neverlessspigot.config.NeverLessSpigotConfig;
import net.md_5.bungee.api.ChatColor;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.WorldServer;

// Implements a Mob AI toggle command
public class MobAICommand extends Command {
	
	private boolean globalAI = NeverLessSpigotConfig.mobAi;

	public MobAICommand() {
		super("mobai");
		this.description = "Toggles Mob AI";
		this.usageMessage = "/mobai";
		this.setPermission("neverlessspigot.mobai");
	}

	@Override
	public boolean execute(CommandSender sender, String currentAlias, String[] args) {
		if (!testPermission(sender)) {
			return true;
		}

		globalAI = !globalAI;
		
		for (WorldServer world : MinecraftServer.getServer().worlds) {
			world.nachoSpigotConfig.enableMobAI = globalAI;
			world.nachoSpigotConfig.set("entity.mob-ai", globalAI);
		}

		NeverLessSpigotConfig.mobAi = globalAI;
		NeverLessSpigotConfig.set("settings.mob-ai-enabled", globalAI);
		
		String status = globalAI ? "enabled" : "disabled";

		sender.sendMessage((globalAI ? ChatColor.GREEN : ChatColor.RED) + "Mob AI is now " + status + " in all worlds.");

		return true;
	}

}
