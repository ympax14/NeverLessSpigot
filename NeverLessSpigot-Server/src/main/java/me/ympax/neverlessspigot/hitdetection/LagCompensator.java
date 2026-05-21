package me.ympax.neverlessspigot.hitdetection;

import java.util.ArrayDeque;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import me.ympax.neverlessspigot.config.NeverLessSpigotConfig;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;

/**
 * Records player position snapshots and allows rewinding to a past position
 * to perform lag-compensated hit detection.
 *
 * Thread-safe: registerMovement() runs on the main thread; getHistoryLocation()
 * may be called from async combat threads.
 */
public class LagCompensator {

    // Extra buffer added to the ping-based rewind to account for server-side
    // scheduling jitter and packet processing delay.
    private static final int PING_OFFSET_MS = 75;

    // Maximum history depth per player (at ~20 Hz movement = 2 seconds).
    private static final int HISTORY_SIZE = 40;

    // Minimum time between recorded snapshots; avoids flooding the buffer when
    // the player is standing still and sending redundant movement packets.
    private static final int MIN_INTERVAL_MS = 25;

    private static final class Snapshot {
        final double x, y, z;
        final long time;

        Snapshot(double x, double y, double z, long time) {
            this.x = x;
            this.y = y;
            this.z = z;
            this.time = time;
        }
    }

    // ConcurrentHashMap for lock-free per-player bucket lookup.
    // Each ArrayDeque is guarded by synchronized(deque).
    private final ConcurrentHashMap<UUID, ArrayDeque<Snapshot>> history = new ConcurrentHashMap<>();

    /**
     * Returns the interpolated position of {@code player} as it appeared to an
     * attacker {@code rewindMs} milliseconds ago (plus the built-in ping offset).
     * Safe to call from any thread.
     */
    public Location getHistoryLocation(Player player, int rewindMs) {
        ArrayDeque<Snapshot> deque = history.get(player.getUniqueId());
        if (deque == null) return player.getLocation();

        long targetTime = System.currentTimeMillis() - rewindMs - PING_OFFSET_MS;

        synchronized (deque) {
            if (deque.isEmpty()) return player.getLocation();

            // Convert deque to array for indexed access during interpolation.
            Snapshot[] snaps = deque.toArray(new Snapshot[0]);
            int len = snaps.length;

            // Walk from newest to oldest to find the bracket around targetTime.
            for (int i = len - 1; i >= 0; i--) {
                if (snaps[i].time <= targetTime) {
                    if (i == len - 1) {
                        // Target time is beyond the newest snapshot — return newest.
                        return toLocation(player.getWorld(), snaps[i], player);
                    }
                    // Interpolate linearly between snaps[i] and snaps[i+1].
                    Snapshot a = snaps[i];
                    Snapshot b = snaps[i + 1];
                    double t = (double) (targetTime - a.time) / (b.time - a.time);
                    t = Math.max(0.0, Math.min(1.0, t));
                    return toLocation(player.getWorld(),
                        a.x + (b.x - a.x) * t,
                        a.y + (b.y - a.y) * t,
                        a.z + (b.z - a.z) * t,
                        player);
                }
            }
            // All snapshots are newer than targetTime — return the oldest.
            return toLocation(player.getWorld(), snaps[0], player);
        }
    }

    /** Called from the main thread every time a player sends a movement packet. */
    public void registerMovement(Player player, Location to) {
        if (!NeverLessSpigotConfig.improvedHitDetection) return;

        long now = System.currentTimeMillis();
        ArrayDeque<Snapshot> deque = history.computeIfAbsent(
            player.getUniqueId(), k -> new ArrayDeque<>(HISTORY_SIZE + 2));

        synchronized (deque) {
            if (!deque.isEmpty() && now - deque.peekLast().time < MIN_INTERVAL_MS) return;

            deque.addLast(new Snapshot(to.getX(), to.getY(), to.getZ(), now));

            if (deque.size() > HISTORY_SIZE) deque.pollFirst();
        }
    }

    public void clearCache(Player player) {
        history.remove(player.getUniqueId());
    }

    // -------------------------------------------------------------------------

    private static Location toLocation(World world, Snapshot s, Player ref) {
        return new Location(world, s.x, s.y, s.z,
            ref.getLocation().getYaw(), ref.getLocation().getPitch());
    }

    private static Location toLocation(World world, double x, double y, double z, Player ref) {
        return new Location(world, x, y, z,
            ref.getLocation().getYaw(), ref.getLocation().getPitch());
    }
}
