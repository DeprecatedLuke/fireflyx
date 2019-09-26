package com.ngxdev.anticheat.handler.handler;

import com.ngxdev.anticheat.utils.world.types.SimpleCollisionBox;
import com.ngxdev.anticheat.utils.Reflective;
import lombok.SneakyThrows;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import java.lang.reflect.Method;

public class PlayerSizeHandlerModern implements PlayerSizeHandler {

    private final Method width;
    private final Method height;
    private final Method gliding;

    public PlayerSizeHandlerModern() {
        width = Reflective._getMethod(Entity.class,"getWidth");
        height = Reflective._getMethod(Entity.class,"getHeight");
        gliding = Reflective._getMethod(Entity.class,"isGliding");
    }

    @Override
    @SneakyThrows
    public double height(Player player) {
        return (double) height.invoke(player);
    }

    @Override
    @SneakyThrows
    public double width(Player player) {
        return (double) width.invoke(player);
    }

    @Override
    @SneakyThrows
    public boolean isGliding(Player player) {
        return (boolean) gliding.invoke(player);
    }

    @SneakyThrows
    public SimpleCollisionBox bounds(Player player) {
        Location l = player.getLocation();
        double width = (double) this.width.invoke(player)/2;
        double height = (double) this.height.invoke(player);
        return new SimpleCollisionBox().offset(l.getX(), l.getY(), l.getZ()).expand(width,0,width).expandMax(0,height,0);
    }

    @Override
    @SneakyThrows
    public SimpleCollisionBox bounds(Player player, double x, double y, double z) {
        double width = (double) this.width.invoke(player)/2;
        double height = (double) this.height.invoke(player);
        return new SimpleCollisionBox().offset(x,y,z).expand(width,0,width).expandMax(0,height,0);
    }

}
