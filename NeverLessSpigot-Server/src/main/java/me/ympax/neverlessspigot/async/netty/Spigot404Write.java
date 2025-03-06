package me.ympax.neverlessspigot.async.netty;

import com.google.common.collect.Queues;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import net.minecraft.server.Packet;

import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;

public class Spigot404Write {
    private static final ConcurrentHashMap<Channel, Queue<PacketQueue>> packetsQueueMap = new ConcurrentHashMap<>();
    private static final ConcurrentHashMap<Channel, Tasks> tasksMap = new ConcurrentHashMap<>();
    private final Channel channel;

    public Spigot404Write(Channel channel) {
        this.channel = channel;
        // Initialise la file de paquets et la gestion des tâches pour ce joueur
        packetsQueueMap.putIfAbsent(channel, Queues.newConcurrentLinkedQueue());
        tasksMap.putIfAbsent(channel, new Tasks());
    }

    public static void clean(Channel channel) {
        if (tasksMap.contains(channel)) tasksMap.remove(channel);
        if (packetsQueueMap.contains(channel)) packetsQueueMap.remove(channel);
    }

    public static void writeThenFlush(Channel channel, Packet<?> value, GenericFutureListener<? extends Future<? super Void>>[] listener) {
        Queue<PacketQueue> packetQueue = packetsQueueMap.computeIfAbsent(channel, k -> Queues.newConcurrentLinkedQueue());
        Tasks tasks = tasksMap.computeIfAbsent(channel, k -> new Tasks());

        packetQueue.add(new PacketQueue(value, listener));

        if (!tasks.addTask()) {
            return;
        }

        try {
            Spigot404Write writer = new Spigot404Write(channel);
            channel.pipeline().lastContext().executor().execute(writer::writeQueueAndFlush);
        } catch (NullPointerException ignored) {
            // Le joueur peut quitter avant que le packet ne soit envoyé
        }
    }

    public void writeQueueAndFlush() {
        Tasks tasks = tasksMap.get(channel);
        Queue<PacketQueue> packetsQueue = packetsQueueMap.get(channel);

        if (tasks == null || packetsQueue == null) {
            return; // Évite une erreur si le joueur s'est déconnecté
        }

        while (tasks.fetchTask()) {
            while (!packetsQueue.isEmpty()) {
                PacketQueue message = packetsQueue.poll();
                if (message == null) continue;

                ChannelFuture future = this.channel.write(message.getPacket());
                if (message.getListener() != null) {
                    future.addListeners(message.getListener());
                }

                future.addListener(ChannelFutureListener.FIRE_EXCEPTION_ON_FAILURE);
            }
        }
        this.channel.flush();
    }

    private static class PacketQueue {
        private final Packet<?> item;
        private final GenericFutureListener<? extends Future<? super Void>>[] listener;

        private PacketQueue(Packet<?> item, GenericFutureListener<? extends Future<? super Void>>[] listener) {
            this.item = item;
            this.listener = listener;
        }

        public Packet<?> getPacket() {
            return this.item;
        }

        public GenericFutureListener<? extends Future<? super Void>>[] getListener() {
            return this.listener;
        }
    }
}