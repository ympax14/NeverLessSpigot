package me.ympax.neverlessspigot.hitdetection;

import java.util.ArrayDeque;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import me.ympax.neverlessspigot.config.NeverLessSpigotConfig;

public class CPSLimiter {
    // In tickless mode canAttack() is called from the Netty thread (fast path),
    // so ConcurrentHashMap + synchronized(deque) is required for thread safety.
    private final ConcurrentHashMap<UUID, ArrayDeque<Long>> timestamps = new ConcurrentHashMap<>();

    public boolean canAttack(UUID playerId) {
        if (NeverLessSpigotConfig.cpsLimit == 0) return true;

        long now = System.currentTimeMillis();
        long windowStart = now - 1000L;

        ArrayDeque<Long> deque = timestamps.computeIfAbsent(playerId, k -> new ArrayDeque<>(32));

        synchronized (deque) {
            while (!deque.isEmpty() && deque.peekFirst() < windowStart) {
                deque.pollFirst();
            }
            if (deque.size() >= NeverLessSpigotConfig.cpsLimit) return false;
            deque.addLast(now);
            return true;
        }
    }

    public void clearPlayer(UUID playerId) {
        timestamps.remove(playerId);
    }
}
