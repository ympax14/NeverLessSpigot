package org.spigotmc;

import java.lang.management.ManagementFactory;
import com.sun.management.OperatingSystemMXBean;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import me.ympax.neverlessspigot.NeverLessSpigot;
import me.ympax.neverlessspigot.config.NeverLessSpigotConfig;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.WorldServer;

public class TicksPerSecondCommand extends Command {
	public TicksPerSecondCommand() {
		super("tps");
		this.description = "Gets the current ticks per second for the server";
		this.usageMessage = ChatColor.RED.toString() + "/tps";
		this.setPermission("bukkit.command.tps");
	}

	@Override
	public boolean execute(CommandSender sender, String currentAlias, String[] args) {
		if (!testPermission(sender)) {
			return true;
		}

		// PaperSpigot start - Further improve tick handling
		double[] tps = org.bukkit.Bukkit.spigot().getTPS();
		String[] tpsAvg = new String[tps.length];

		for (int i = 0; i < tps.length; i++) tpsAvg[i] = format(tps[i]);
	
		// NeverLessSpigot - more detailed tps cmd
		int entityCount = 0;
		
		for (WorldServer world : MinecraftServer.getServer().worlds) {
			entityCount = entityCount + world.entityList.size();
		}
		
		int tileEntityCount = 0;
		
		for (WorldServer world : MinecraftServer.getServer().worlds) {
			tileEntityCount = tileEntityCount + world.tileEntityList.size();
		}

		boolean mobAi = MinecraftServer.getServer().worlds.get(0).nachoSpigotConfig.enableMobAI;

		String message = ChatColor.DARK_PURPLE.toString() + "NeverLessSpigot Performance:\n";
			message += ChatColor.DARK_PURPLE + "├ " + ChatColor.GOLD + "System \n";

        OperatingSystemMXBean osBean = (OperatingSystemMXBean) ManagementFactory.getOperatingSystemMXBean();
        
        double cpuUsage = osBean.getSystemCpuLoad() * 100;
        	message += ChatColor.DARK_PURPLE + "├─ CPU Usage: " + ChatColor.LIGHT_PURPLE + String.format("%.2f", cpuUsage) + "%\n";

        int availableCores = osBean.getAvailableProcessors();
        	message += ChatColor.DARK_PURPLE + "├─ Available Cores: " + ChatColor.LIGHT_PURPLE + availableCores + "\n";

			message += ChatColor.DARK_PURPLE + "├─ Current Memory Usage: " + ChatColor.LIGHT_PURPLE
    			+ ((Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()) / (1024 * 1024)) + "/"
    			+ (Runtime.getRuntime().totalMemory() / (1024 * 1024)) + " mb (Max: "
    			+ (Runtime.getRuntime().maxMemory() / (1024 * 1024)) + " mb)\n";

			message += ChatColor.DARK_PURPLE + "├ " + ChatColor.GOLD + "Server (Global)\n";
			message += ChatColor.DARK_PURPLE + "├─ TPS from last 1m, 5m, 15m: " + org.apache.commons.lang.StringUtils.join(tpsAvg, ", ") + "\n";
			message += ChatColor.DARK_PURPLE + "├─ Online Players: " + ChatColor.LIGHT_PURPLE + Bukkit.getOnlinePlayers().size() + "\n";
			message += ChatColor.DARK_PURPLE + "├─ Entity Count: " + ChatColor.LIGHT_PURPLE + entityCount + "\n";
			message += ChatColor.DARK_PURPLE + "├─ Tile Entity Count: " + ChatColor.LIGHT_PURPLE + tileEntityCount + "\n";
			message += ChatColor.DARK_PURPLE + "├─ Mob AI: " + (mobAi ? ChatColor.GREEN : ChatColor.RED) + mobAi + "\n";
			message += ChatColor.DARK_PURPLE + "├─ Milliseconds to Run Last Tick: " + ChatColor.LIGHT_PURPLE + Math.round(MinecraftServer.getServer().getLastMspt() * 100.0) / 100.0 + "\n";

			// Informations sur les fonctionnalités Asynchrone et Tickless
			message += ChatColor.DARK_PURPLE + "├ " + ChatColor.GOLD + "Asynchrone/Tickless \n";
			message += ChatColor.DARK_PURPLE + "├─ Async Combat: " + (NeverLessSpigotConfig.asyncCombat ? ChatColor.GREEN : ChatColor.RED) + NeverLessSpigotConfig.asyncCombat + "\n";
			message += ChatColor.DARK_PURPLE + "├─ Tickless Combat: " + (NeverLessSpigotConfig.ticklessCombat ? ChatColor.GREEN : ChatColor.RED) + NeverLessSpigotConfig.ticklessCombat + "\n";

		if (NeverLessSpigot.getInstance().getKnockbackThread() != null && NeverLessSpigot.getInstance().getKnockbackThread().isRunning()) {
			message += ChatColor.DARK_PURPLE + "\n├ " + ChatColor.GOLD + "Knockback Thread\n";
			String tpsResult = "";

			if (NeverLessSpigotConfig.ticklessCombat == true) {
				tpsResult = "Tickless";
			} else {

				double[] kbTps = NeverLessSpigot.getInstance().getKnockbackThread().getTPS();
				String[] tpsAvgKb = new String[kbTps.length];

				tpsResult = org.apache.commons.lang.StringUtils.join(tpsAvgKb, ", ");
			}
			
			message += ChatColor.DARK_PURPLE + "├─ TPS from last 1m, 5m, 15m: " + tpsResult + "\n";
			message += ChatColor.DARK_PURPLE + "├─ Milliseconds to Run Last Tick: " + ChatColor.LIGHT_PURPLE + Math.round(NeverLessSpigot.getInstance().getKnockbackThread().getLastMspt() * 100.0) / 100.0;
		}

		if (NeverLessSpigot.getInstance().getHitDetectionThread() != null && NeverLessSpigot.getInstance().getHitDetectionThread().isRunning()) {
			message += ChatColor.DARK_PURPLE + "\n├ " + ChatColor.GOLD + "HitDetection Thread\n";
			String tpsResult = "";

			if (NeverLessSpigotConfig.ticklessCombat == true) {
				tpsResult = "Tickless";
			} else {

				double[] hitDetectTps = NeverLessSpigot.getInstance().getHitDetectionThread().getTPS();
				String[] tpsAvgHitDetectTps = new String[hitDetectTps.length];

				tpsResult = org.apache.commons.lang.StringUtils.join(tpsAvgHitDetectTps, ", ");
			}
			
			message += ChatColor.DARK_PURPLE + "├─ TPS from last 1m, 5m, 15m: " + tpsResult + "\n";
			message += ChatColor.DARK_PURPLE + "├─ Milliseconds to Run Last Tick: " + ChatColor.LIGHT_PURPLE + Math.round(NeverLessSpigot.getInstance().getHitDetectionThread().getLastMspt() * 100.0) / 100.0;
		}

		sender.sendMessage(message);

		/*sender.sendMessage(ChatColor.DARK_PURPLE + "├ Server TPS from last 1m, 5m, 15m: " + org.apache.commons.lang.StringUtils.join(tpsAvg, ", "));
		
		if (NeverLessSpigot.getInstance().getKnockbackThread() != null && NeverLessSpigot.getInstance().getKnockbackThread().isRunning()) {
			double[] kbTps = NeverLessSpigot.getInstance().getKnockbackThread().getTPS();
			String[] tpsAvgKb = new String[kbTps.length];

			for (int i = 0; i < kbTps.length; i++) tpsAvgKb[i] = format(kbTps[i]);
			sender.sendMessage(ChatColor.DARK_PURPLE + "├ Knockback Thread TPS from last 1m, 5m, 15m: " + org.apache.commons.lang.StringUtils.join(tpsAvgKb, ", "));
		}
		
		sender.sendMessage(ChatColor.DARK_PURPLE + "├ Current Memory Usage: " + ChatColor.LIGHT_PURPLE
				+ ((Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()) / (1024 * 1024)) + "/"
				+ (Runtime.getRuntime().totalMemory() / (1024 * 1024)) + " mb (Max: "
				+ (Runtime.getRuntime().maxMemory() / (1024 * 1024)) + " mb)");
		sender.sendMessage(ChatColor.DARK_PURPLE + "├ Online Players: " + ChatColor.LIGHT_PURPLE + Bukkit.getOnlinePlayers().size());
		sender.sendMessage(ChatColor.DARK_PURPLE + "├ Entity Count: " + ChatColor.LIGHT_PURPLE + entityCount);
		sender.sendMessage(ChatColor.DARK_PURPLE + "├ Tile Entity Count: " + ChatColor.LIGHT_PURPLE + tileEntityCount);

		boolean mobAi = MinecraftServer.getServer().worlds.get(0).nachoSpigotConfig.enableMobAI;

		sender.sendMessage(ChatColor.DARK_PURPLE + "├ Mob AI: " + (mobAi ? ChatColor.GREEN : ChatColor.RED) + mobAi);
		sender.sendMessage(ChatColor.DARK_PURPLE + "├ Milliseconds to Run Last Tick: " + ChatColor.LIGHT_PURPLE + Math.round(MinecraftServer.getServer().getLastMspt() * 100.0) / 100.0);
		*/
		return true;
	}

	private static String format(double tps) // PaperSpigot - made static
	{
		return ChatColor.LIGHT_PURPLE.toString() + Math.round(tps);
		//((tps >= NeverLessSpigotConfig.combatThreadTPS-(NeverLessSpigotConfig.combatThreadTPS/10)) ? ChatColor.LIGHT_PURPLE : (tps > 18.0) ? ChatColor.GREEN : (tps > 16.0) ? ChatColor.YELLOW : ChatColor.RED).toString() + Math.round(tps); // NeverLessSpigot - Print exact TPS
	}
}
