package xyz.sculas.nacho.anticrash;

import me.ympax.neverlessspigot.handler.PacketHandler;
import net.minecraft.server.ChatMessage;
import net.minecraft.server.Packet;
import net.minecraft.server.PacketDataSerializer;
import net.minecraft.server.PacketPlayInCustomPayload;
import net.minecraft.server.PlayerConnection;

public class AntiCrash implements PacketHandler {
	@Override
	public boolean onReceivedPacket(PlayerConnection playerConnection, Packet packet) {
		if (packet instanceof PacketPlayInCustomPayload) {
			PacketDataSerializer ab = ((PacketPlayInCustomPayload) packet).b();
			if (ab.refCnt() < 1) {
				playerConnection.getNetworkManager().close(new ChatMessage("Wrong ref count!"));
				return false;
			}
			if (ab.readableBytes() > 25780) {
				playerConnection.getNetworkManager().close(new ChatMessage("Readable bytes exceeds limit!"));
				return false;
			}
			// ty Lew_x :)
			/*
			 * if (ab.capacity() > 25780 || ab.capacity() < 1) {
			 * playerConnection.getNetworkManager().close(new
			 * ChatMessage("Wrong capacity!")); return false; }
			 */
		}
		return true;
	}
}
