// From
// https://github.com/Argarian-Network/NachoSpigot/tree/async-kb-hit
package me.ympax.neverlessspigot.async.thread;

import me.ympax.neverlessspigot.config.NeverLessSpigotConfig;

public class CombatThread extends AsyncOutPacketThread {
    public CombatThread(String s) {
        super(s, NeverLessSpigotConfig.combatThreadTPS);
    }

    // Handle packets
    @Override
    public void run() {
        while (this.packets.size() > 0) {
            this.packets.poll().run();
        }
    }
} 
