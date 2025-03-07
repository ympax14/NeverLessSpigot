// From
// https://github.com/Argarian-Network/NachoSpigot/tree/async-kb-hit
package me.ympax.neverlessspigot.async.thread;

import me.ympax.neverlessspigot.config.NeverLessSpigotConfig;

public class CombatThread extends AsyncThread {
    public CombatThread(String s) {
        super(s, NeverLessSpigotConfig.combatThreadTPS);
    }

    // Handle packets and tasks
    @Override
    public void run() {
        while (this.tasks.size() > 0) {
            this.tasks.poll().run();
        }
    }
} 
