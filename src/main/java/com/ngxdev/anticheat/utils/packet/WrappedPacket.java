package com.ngxdev.anticheat.utils.packet;

import com.ngxdev.anticheat.utils.Reflective;
import com.ngxdev.anticheat.utils.Utils;
import lombok.SneakyThrows;
import org.bukkit.Bukkit;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

public class WrappedPacket {

    public static String VERSION = Bukkit.getServer().getClass().getName().split("\\.")[3];
    public static Class<?> particalPacket = c("net.minecraft.server."+VERSION+".PacketPlayOutWorldParticles");

    @SneakyThrows
    public static Class c(String name) {
        return Utils.class.getClassLoader().loadClass(name);
    }

    private transient Class packet;

    public WrappedPacket(Class packet) {
        this.packet = packet;
    }

    @SneakyThrows
    public Object getPacket() {
        Object packet = this.packet.newInstance();
        for (Field f : this.getClass().getDeclaredFields()) {
            if (!Modifier.isTransient(f.getModifiers())&&!Modifier.isStatic(f.getModifiers())) {
                f.setAccessible(true);
                Reflective.set(packet,f.getName(),f.get(this));
            }
        }
        return packet;
    }
}
