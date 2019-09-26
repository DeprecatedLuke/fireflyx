package com.ngxdev.anticheat.data;

import com.ngxdev.anticheat.data.playerdata.PlayerData;
import com.ngxdev.tinyprotocol.packet.in.WrappedInFlyingPacket;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerMoveEvent;

@AllArgsConstructor
@Getter
public class SimpleLocation {
    double x, y, z;
    float yaw, pitch;


    public SimpleLocation(double x, double y, double z) {
        this(x, y, z, 0, 0);
    }

    public SimpleLocation(PlayerData data) {
        this.x = data.movement.tx;
        this.y = data.movement.ty;
        this.z = data.movement.tz;
        this.yaw = data.movement.tyaw;
        this.pitch = data.movement.tpitch;
    }

    public boolean equals(WrappedInFlyingPacket packet) {
        return x == packet.getX() && y == packet.getY() && z == packet.getZ() && yaw == packet.getYaw() && pitch == packet.getPitch();
    }

    public boolean equalsLenient(WrappedInFlyingPacket packet) {
        double dx = packet.getX() - x;
        double dy = packet.getY() - y;
        double dz = packet.getZ() - z;
        double dist = Math.abs(dx + dy + dz);
        System.out.println(dist);
        return dist < 0.0625D;
    }

    public boolean equalsLenient(PlayerMoveEvent e) {
        double dx = e.getTo().getX() - x;
        double dy = e.getTo().getY() - y;
        double dz = e.getTo().getZ() - z;
        double dist = dx * dx + dy * dy + dz * dz;
        return dist < 0.07D;
    }


    public int getBlockX() {
        return floor(this.x);
    }

    public int getBlockY() {
        return floor(this.y);
    }

    public int getBlockZ() {
        return floor(this.z);
    }

    private static int floor(double num) {
        int floor = (int) num;
        return (double) floor == num ? floor : floor - (int) (Double.doubleToRawLongBits(num) >>> 63);
    }

    public void teleport(Player player) {
        player.teleport(new Location(player.getWorld(), x, y, z, player.getLocation().getYaw(), player.getLocation().getPitch()));
    }

    public Location toLocation(World world) {
        return new Location(world, x, y, z, yaw, pitch);
    }
}
