package me.ympax.neverlessspigot.handler;

import org.bukkit.Location;
import org.bukkit.entity.Player;

import net.minecraft.server.PacketPlayInFlying;

public interface MovementHandler {
	void updateLocation(Player paramPlayer, Location paramLocation1, Location paramLocation2, PacketPlayInFlying paramPacketPlayInFlying);

	void updateRotation(Player paramPlayer, Location paramLocation1, Location paramLocation2, PacketPlayInFlying paramPacketPlayInFlying);
}
