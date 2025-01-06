package me.ympax.neverlessspigot.handler;

import org.bukkit.Location;
import org.bukkit.entity.Player;

import net.minecraft.server.PacketPlayInFlying;

public interface MovementHandler {
	default boolean updateLocation(Player paramPlayer, Location to, Location from, PacketPlayInFlying paramPacketPlayInFlying) {
		return true;
	}

	default boolean updateRotation(Player paramPlayer, Location to, Location from, PacketPlayInFlying paramPacketPlayInFlying) {
		return true;
	}
}
