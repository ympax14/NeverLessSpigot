package net.minecraft.server;

import java.io.IOException;

public class PacketPlayOutEntityVelocity implements Packet<PacketListenerPlayOut> {
    private org.bukkit.entity.Entity entity;
    private int a;
    private int b;
    private int c;
    private int d;

    private org.bukkit.entity.Entity getEntityById(int id) {
        org.bukkit.entity.Entity toReturn = null;
        for (org.bukkit.World w : org.bukkit.Bukkit.getWorlds()) {
            for (org.bukkit.entity.Entity e : w.getEntities()) {
                if (e.getEntityId() == id) toReturn = e;
            }
        }

        return toReturn;
   }

    public PacketPlayOutEntityVelocity() {
    }

    public PacketPlayOutEntityVelocity(Entity var1) {
        this(var1.getId(), var1.motX, var1.motY, var1.motZ);
    }

    public PacketPlayOutEntityVelocity(int var1, double var2, double var4, double var6) {
        this.entity = getEntityById(var1);
        this.a = var1;
        double var8 = 3.9D;
        if (var2 < -var8) {
            var2 = -var8;
        }

        if (var4 < -var8) {
            var4 = -var8;
        }

        if (var6 < -var8) {
            var6 = -var8;
        }

        if (var2 > var8) {
            var2 = var8;
        }

        if (var4 > var8) {
            var4 = var8;
        }

        if (var6 > var8) {
            var6 = var8;
        }

        this.b = (int) (var2 * 8000.0D);
        this.c = (int) (var4 * 8000.0D);
        this.d = (int) (var6 * 8000.0D);
    }

    public org.bukkit.entity.Entity getEntity() {
        return this.entity;
    }

    public void a(PacketDataSerializer var1) throws IOException {
        this.a = var1.e();
        this.b = var1.readShort();
        this.c = var1.readShort();
        this.d = var1.readShort();
    }

    public void b(PacketDataSerializer var1) throws IOException {
        var1.b(this.a);
        var1.writeShort(this.b);
        var1.writeShort(this.c);
        var1.writeShort(this.d);
    }

    public void a(PacketListenerPlayOut var1) {
        var1.a(this);
    }

    public int getA() {
        return a;
    }

    public int getB() {
        return b;
    }

    public int getC() {
        return c;
    }

    public int getD() {
        return d;
    }
}