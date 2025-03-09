// From
// https://github.com/Argarian-Network/NachoSpigot/tree/async-kb-hit
package me.ympax.neverlessspigot.async.thread;

import io.netty.channel.Channel;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;

import java.math.BigDecimal;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.LockSupport;

import org.bukkit.Bukkit;

import me.ympax.neverlessspigot.async.netty.Spigot404Write;
import me.ympax.neverlessspigot.config.NeverLessSpigotConfig;
import net.minecraft.server.NetworkManager;
import net.minecraft.server.Packet;

public abstract class AsyncThread {
	private static final long SEC_IN_NANO = 1000000000;

    private volatile boolean running = true;
	private int TPS;
	private long TICK_TIME;
	private long MAX_CATCHUP_BUFFER;
    private Thread thread;
    protected Queue<Runnable> tasks = new ConcurrentLinkedQueue<Runnable>();

	private long lastTickTime = 0;
    private int currentTick = 0;
    private final RollingAverage tps1 = new RollingAverage(60);
    private final RollingAverage tps5 = new RollingAverage(60 * 5);
    private final RollingAverage tps15 = new RollingAverage(60 * 15);

	private long lastPacketOrTaskTime;

	private ExecutorService executor = new ThreadPoolExecutor(4, 10, 30L, TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>());

	private boolean isParked = false;

    public AsyncThread(String s, int tps) {
		TPS = tps;
		TICK_TIME = SEC_IN_NANO / TPS;
		MAX_CATCHUP_BUFFER = TICK_TIME * TPS * 60L;
		lastPacketOrTaskTime = System.nanoTime();

        this.thread = new Thread(new Runnable() {
            @Override
            public void run() {
            	AsyncThread.this.loop();
            }
        }, s);
		this.thread.setDaemon(true);
        this.thread.start();
		Bukkit.getLogger().info("[" + s + "] Started !");
    }

	public ExecutorService getExecutorService() {
		return this.executor;
	}

	public long getLastMspt() {
		return this.lastTickTime;
	}

	public void setTPS(int tps) {
		TPS = tps;
		TICK_TIME = SEC_IN_NANO / TPS;
		MAX_CATCHUP_BUFFER = TICK_TIME * TPS * 60L;
	}

    public void stop() {
        this.running = false;
		LockSupport.unpark(thread);
    }

    public boolean isRunning() {
        return this.running;
    }

    // Loops scanning for new packets to send
	public void loop() {
		long lastTick = System.nanoTime();
		long catchupTime = 0L;

		while (this.running) {
			if (!NeverLessSpigotConfig.ticklessCombat) {
				long curTime = System.nanoTime();
				long wait = TICK_TIME - (curTime - lastTick);
	
				if (wait > 0) {
					if (catchupTime < 2E6) {
						wait += Math.abs(catchupTime);
					} else if (wait < catchupTime) {
						//catchupTime -= wait;
						wait = 0;
					} else {
						wait -= catchupTime;
						//catchupTime = 0;
					}
	
					try {
						// Wait a bit before checking for new packets
						Thread.sleep(wait / 1000000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					//curTime = System.nanoTime();
					catchupTime = 0L;
					continue;
				}
	
				catchupTime = Math.min(MAX_CATCHUP_BUFFER, catchupTime - wait);
	
				// Handle packets
	
				long beforeTick = System.currentTimeMillis();
				this.run();
				lastTickTime = System.currentTimeMillis() - beforeTick;
	
				if (++this.currentTick % TPS == 0 ) {
					final long diff = curTime - lastTick;
	
					BigDecimal TPS_BASE = new BigDecimal(1E9);
					BigDecimal currentTps = TPS_BASE.divide(new BigDecimal(diff * TPS), 30, java.math.RoundingMode.HALF_UP).multiply(new BigDecimal(TPS));
	
					tps1.add(currentTps, diff);
					tps5.add(currentTps, diff);
					tps15.add(currentTps, diff);
				}
	
				lastTick = curTime;

				if (System.nanoTime() - lastPacketOrTaskTime > 60 * SEC_IN_NANO) { // Met en veille au bout de 60 secondes d'inactivé
					this.isParked = true;
					Bukkit.getLogger().info("[" + this.thread.getName() + "] Parked !");
                    LockSupport.park();
                }
			} else {
				this.isParked = true;
				Bukkit.getLogger().info("[" + this.thread.getName() + "] Parked !");
				LockSupport.park();
			}
		}

		executor.shutdown();
        Bukkit.getLogger().info("[" + this.thread.getName() + "] Stopped !");
	}

    public abstract void run();

    public double[] getTPS() {
        return new double[] {
            this.tps1.getAverage(),
            this.tps5.getAverage(),
            this.tps15.getAverage()
        };
    }

    // Queue a packet
    public void addPacket(final Packet<?>  packet, final NetworkManager manager, final GenericFutureListener<? extends Future<? super Void>>[] agenericfuturelistener) {
		lastPacketOrTaskTime = System.nanoTime();
		this.tasks.add(new Runnable() {
            @Override
            public void run() {
                Spigot404Write.writeThenFlush(manager.channel, packet, agenericfuturelistener);
            }
        });
		if (this.isParked) {
			LockSupport.unpark(thread);
			this.isParked = false;
		}
    }

	public void addTask(Runnable runnable) {
		lastPacketOrTaskTime = System.nanoTime();
		this.tasks.add(runnable);
		if (this.isParked) {
			LockSupport.unpark(thread);
			this.isParked = false;
		}
	}

    public Thread getThread() {
        return this.thread;
    }

    // Store packet data
    public static class RunnableItem {
        private Channel channel;
        private Packet<?>  packet;

        public RunnableItem(Channel m, Packet<?>  p) {
            this.channel = m;
            this.packet = p;
        }

        public Packet<?> getPacket() {
            return this.packet;
        }

        public Channel getChannel() {
            return this.channel;
        }
    }

    public class RollingAverage {
	    private final int size;
	    private long time;
	    private java.math.BigDecimal total;
	    private int index = 0;
	    private final java.math.BigDecimal[] samples;
	    private final long[] times;

	    RollingAverage(int size) {
	        this.size = size;
	        this.time = size * SEC_IN_NANO;
	        this.total = dec(TPS).multiply(dec(SEC_IN_NANO)).multiply(dec(size));
	        this.samples = new java.math.BigDecimal[size];
	        this.times = new long[size];
	        for (int i = 0; i < size; i++) {
	            this.samples[i] = dec(TPS);
	            this.times[i] = SEC_IN_NANO;
	        }
	    }

	    private java.math.BigDecimal dec(long t) {
	        return new java.math.BigDecimal(t);
	    }
	    public void add(java.math.BigDecimal x, long t) {
	        time -= times[index];
	        total = total.subtract(samples[index].multiply(dec(times[index])));
	        samples[index] = x;
	        times[index] = t;
	        time += t;
	        total = total.add(x.multiply(dec(t)));
	        if (++index == size) {
	            index = 0;
	        }
	    }

	    public double getAverage() {
	        return total.divide(dec(time), 30, java.math.RoundingMode.HALF_UP).doubleValue();
	    }
	}
} 