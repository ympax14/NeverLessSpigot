package me.ympax.neverlessspigot.hitdetection;

import java.util.*;

import me.ympax.neverlessspigot.config.NeverLessSpigotConfig;

public class CPSLimiter {
    private final Map<UUID, List<Long>> attackTimestamps = new HashMap<>();

    public boolean canAttack(UUID playerId) {
        if (NeverLessSpigotConfig.cpsLimit == 0) return true;

        long currentTime = System.currentTimeMillis();
        attackTimestamps.putIfAbsent(playerId, new ArrayList<>());

        List<Long> timestamps = attackTimestamps.get(playerId);

        timestamps.removeIf(time -> time < currentTime - 1000);

        if (timestamps.size() >= NeverLessSpigotConfig.cpsLimit) {
            return false; 
        }

        timestamps.add(currentTime);
        return true;
    }
}
