package com.ngxdev.anticheat.api.ui.book;

import com.ngxdev.anticheat.utils.Reflective;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import lombok.SneakyThrows;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.lang.reflect.Method;

public class BookHandler {

    public static String VERSION = Bukkit.getServer().getClass().getName().split("\\.")[3];
    public static Class<?> packetSetSlot = c("net.minecraft.server."+VERSION+".PacketPlayOutSetSlot");
    public static Class<?> packetCustompayload = c("net.minecraft.server."+VERSION+".PacketPlayOutCustomPayload");
    public static Class<?> packetDataSerializer = c("net.minecraft.server."+VERSION+".PacketDataSerializer");

    public static Class<?> CraftStack = c("org.bukkit.craftbukkit."+VERSION+".inventory.CraftItemStack");
    public static Class<?> NMSStack = c("net.minecraft.server."+VERSION+".ItemStack");

    private static Method convertStack;

    static {
        try {
            convertStack = CraftStack.getDeclaredMethod("asNMSCopy",ItemStack.class);
            convertStack.setAccessible(true);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
    }

    public static void sendBook(Player player, ItemStack stack) {
        int slot = player.getInventory().getHeldItemSlot();
        ItemStack old = player.getInventory().getItem(slot);
        slot+=36;
        sendPacket(player,
                createSetSlotPacket(0,slot, asNMSCopy(stack)),
                createCustomPayloadPacket("MC|BOpen",new byte[]{0,0,0,0}),
                createSetSlotPacket(0,slot,asNMSCopy(old)));
    }

    @SneakyThrows
    public static void sendPacket(Player player, Object... packet) {
        Object entity = Reflective.get(player,"entity");
        Object connection = Reflective.get(entity,"playerConnection");
        for (Object p : packet)
            Reflective.call(connection,"sendPacket",p);
    }

    @SneakyThrows
    public static Object createSetSlotPacket(int window, int slot, Object itemStack) {
        return packetSetSlot.getDeclaredConstructor(Integer.TYPE,Integer.TYPE,NMSStack).newInstance(window,slot,itemStack);
    }

    @SneakyThrows
    public static Object createCustomPayloadPacket(String channel, byte[] content) {
        Object pds = packetDataSerializer.getDeclaredConstructor(ByteBuf.class).newInstance(Unpooled.wrappedBuffer(content));
        return packetCustompayload.getDeclaredConstructor(String.class,packetDataSerializer).newInstance(channel,pds);
    }

    @SneakyThrows
    public static Class c(String s) {
        return Class.forName(s,true,Bukkit.class.getClassLoader());
    }

    @SneakyThrows
    public static Object asNMSCopy(ItemStack item) {
        return convertStack.invoke(null,item);
    }

}

