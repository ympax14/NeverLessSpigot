package dev.cobblesword.nachospigot;

import java.util.Set;

import me.ympax.neverlessspigot.NeverLessSpigot;
import me.ympax.neverlessspigot.handler.MovementHandler;
import me.ympax.neverlessspigot.handler.PacketHandler;

@Deprecated
public class Nacho {

	private static Nacho INSTANCE;

	public Nacho() {
		INSTANCE = this;
	}

	public static Nacho get() {
		return INSTANCE == null ? new Nacho() : INSTANCE;
	}

	public void registerCommands() {

	}

	public void registerPacketListener(PacketHandler packetListener) {
		NeverLessSpigot.getInstance().registerPacketHandler(packetListener);
	}

	public void unregisterPacketListener(PacketHandler packetListener) {
		NeverLessSpigot.getInstance().unregisterPacketHandler(packetListener);
	}

	public Set<PacketHandler> getPacketListeners() {
		return NeverLessSpigot.getInstance().getPacketListeners();
	}

	public void registerMovementHandler(MovementHandler movementListener) {
		NeverLessSpigot.getInstance().registerMovementHandler(movementListener);
	}

	public void unregisterMovementHandler(MovementHandler movementListener) {
		NeverLessSpigot.getInstance().unregisterMovementHandler(movementListener);
	}

	public Set<MovementHandler> getMovementListeners() {
		return NeverLessSpigot.getInstance().getMovementHandlers();
	}

}
