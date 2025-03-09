package me.ympax.neverlessspigot;

import java.util.Set;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bukkit.Bukkit;
import org.bukkit.command.SimpleCommandMap;

import com.google.common.collect.Sets;
import com.google.common.util.concurrent.ThreadFactoryBuilder;

import me.ympax.neverlessspigot.async.AsyncUtil;
import me.ympax.neverlessspigot.async.pathsearch.SearchHandler;
import me.ympax.neverlessspigot.async.thread.CombatThread;
import me.ympax.neverlessspigot.commands.*;
import me.ympax.neverlessspigot.config.NeverLessSpigotConfig;
import me.ympax.neverlessspigot.handler.MovementHandler;
import me.ympax.neverlessspigot.handler.PacketHandler;
import me.ympax.neverlessspigot.hitdetection.LagCompensator;
import me.ympax.neverlessspigot.statistics.StatisticsClient;
import net.minecraft.server.MinecraftServer;
import xyz.sculas.nacho.anticrash.AntiCrash;
import xyz.sculas.nacho.async.AsyncExplosions;

public class NeverLessSpigot {

	private StatisticsClient client;
	
	public static final Logger LOGGER = LogManager.getLogger();
	private static final Logger DEBUG_LOGGER = LogManager.getLogger();
	private static NeverLessSpigot INSTANCE;
	
	private CombatThread knockbackThread;
	private CombatThread hitDetectionThread;
	
	private final Executor statisticsExecutor = Executors
			.newSingleThreadExecutor(new ThreadFactoryBuilder().setNameFormat("NeverLessSpigot Statistics Thread")
			.build());
	
	private volatile boolean statisticsEnabled = false;
	
	private LagCompensator lagCompensator;
	
	private final Set<PacketHandler> packetHandlers = Sets.newConcurrentHashSet();
	private final Set<MovementHandler> movementHandlers = Sets.newConcurrentHashSet();

	public NeverLessSpigot() {
		INSTANCE = this;
		this.init();
	}

	public void reload() {
		this.init();
	}

	private void initCmds() {
		
		SimpleCommandMap commandMap = MinecraftServer.getServer().server.getCommandMap();
		
		if (NeverLessSpigotConfig.mobAiCmd) {
			MobAICommand mobAiCommand = new MobAICommand();
			commandMap.register(mobAiCommand.getName(), "ns", mobAiCommand);
		}
		
		if (NeverLessSpigotConfig.pingCmd) {
			PingCommand pingCommand = new PingCommand();
			commandMap.register(pingCommand.getName(), "ns", pingCommand);
		}
	
		
		
		// NachoSpigot commands
		// TODO: add configuration for all of these
		SetMaxSlotCommand setMaxSlotCommand = new SetMaxSlotCommand(); // [Nacho-0021] Add setMaxPlayers within Bukkit.getServer() and SetMaxSlot Command
		commandMap.register(setMaxSlotCommand.getName(), "ns", setMaxSlotCommand);

		SpawnMobCommand spawnMobCommand = new SpawnMobCommand();
		commandMap.register(spawnMobCommand.getName(), "ns", spawnMobCommand);

		KnockbackCommand knockbackCommand = new KnockbackCommand();
		commandMap.register(knockbackCommand.getName(), "ns", knockbackCommand);

		AsyncCombatCommand asyncKnockbackCommand = new AsyncCombatCommand();
		commandMap.register(asyncKnockbackCommand.getName(), "ns", asyncKnockbackCommand);

		TicklessCombatCommand ticklessCombatCommand = new TicklessCombatCommand();
		commandMap.register(ticklessCombatCommand.getName(), "ns", ticklessCombatCommand);

		CombatTPSCommand combatTPSCommand = new CombatTPSCommand();
		commandMap.register(combatTPSCommand.getName(), "ns", combatTPSCommand);

		KillEntitiesCommand killEntitiesCommand = new KillEntitiesCommand();
		commandMap.register(killEntitiesCommand.getName(), "ns", killEntitiesCommand);
	}

	private void initStatistics() {
		if (NeverLessSpigotConfig.statistics && !statisticsEnabled) {
			Runnable statisticsRunnable = (() -> {
				client = new StatisticsClient();
				try {
					statisticsEnabled = true;

					if (!client.isConnected) {
						// Connect to the statistics server and notify that there is a new server
						client.start("127.0.0.1", 500);
						client.sendMessage("new server");

						while (true) {
							// Keep alive, this tells the statistics server that this server
							// is still online
							client.sendMessage("keep alive packet");

							// Online players, this tells the statistics server how many players
							// are on
							client.sendMessage("player count packet " + Bukkit.getOnlinePlayers().size());

							// Statistics are sent every 40 secs.
							TimeUnit.SECONDS.sleep(40);
						}

					}
				} catch (Exception ignored) {}
			});
			AsyncUtil.run(statisticsRunnable, statisticsExecutor);
		}
	}

	public void startAsyncThreads() {
		if (knockbackThread != null && knockbackThread.isRunning()) {
			try {        
				knockbackThread.stop();     
				knockbackThread.getThread().join(); 
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

		if (hitDetectionThread != null && hitDetectionThread.isRunning()) {
			try {        
				hitDetectionThread.stop();     
				hitDetectionThread.getThread().join(); 
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

		knockbackThread = new CombatThread("Knockback Thread");
		hitDetectionThread = new CombatThread("HitDetection Thread");
	}

	private void init() {
		initCmds();
		initStatistics();
		
		// We do not want to initialize this again after a reload
		if (NeverLessSpigotConfig.asyncPathSearches && SearchHandler.getInstance() == null) {
			new SearchHandler();
		}
		
		if (NeverLessSpigotConfig.asyncCombat || NeverLessSpigotConfig.ticklessCombat) {
			startAsyncThreads();
		}
		lagCompensator = new LagCompensator();	
		if (NeverLessSpigotConfig.asyncTnt) {
			AsyncExplosions.initExecutor(NeverLessSpigotConfig.fixedPoolSize);
		}
		if (NeverLessSpigotConfig.enableAntiCrash) {
			registerPacketHandler(new AntiCrash());
		}
	}

	public StatisticsClient getClient() {
		return this.client;
	}

	public CombatThread getHitDetectionThread() {
		return hitDetectionThread;
	}
	
	public CombatThread getKnockbackThread() {
		return knockbackThread;
	}
	
    public LagCompensator getLagCompensator() {
        return lagCompensator;
    }
    
	public static void debug(String msg) {
		if (NeverLessSpigotConfig.debugMode)
			DEBUG_LOGGER.info(msg);
	}
	
	public void registerPacketHandler(PacketHandler packetHandler) {
		this.packetHandlers.add(packetHandler);
	}

	public void unregisterPacketHandler(PacketHandler packetHandler) {
		this.packetHandlers.remove(packetHandler);
	}

	public Set<PacketHandler> getPacketListeners() {
		return this.packetHandlers;
	}

	public void registerMovementHandler(MovementHandler movementHandler) {
		this.movementHandlers.add(movementHandler);
	}

	public void unregisterMovementHandler(MovementHandler movementHandler) {
		this.movementHandlers.remove(movementHandler);
	}

	public Set<MovementHandler> getMovementHandlers() {
		return this.movementHandlers;
	}
	
	public static NeverLessSpigot getInstance() {
		return INSTANCE;
	}
}
