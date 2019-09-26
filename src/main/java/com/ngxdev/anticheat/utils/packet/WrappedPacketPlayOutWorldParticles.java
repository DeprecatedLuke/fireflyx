package com.ngxdev.anticheat.utils.packet;

import com.ngxdev.anticheat.utils.Reflective;
import lombok.Getter;
import lombok.Setter;

import java.lang.reflect.Field;

public class WrappedPacketPlayOutWorldParticles extends WrappedPacket {

    public static Class<?> particalPacket = c("net.minecraft.server."+VERSION+".PacketPlayOutWorldParticles");
    public static Class<?> enumParticle = c("net.minecraft.server."+VERSION+".EnumParticle");
    private static Field particalName = Reflective.findField(enumParticle,String.class);

    private Object a;
    @Getter @Setter private float b;
    @Getter @Setter private float c;
    @Getter @Setter private float d;
    @Getter @Setter private float e;
    @Getter @Setter private float f;
    @Getter @Setter private float g;
    @Getter @Setter private float h;
    @Getter @Setter private int i;
    @Getter @Setter private boolean j;
    @Getter @Setter private int[] k;

    public WrappedPacketPlayOutWorldParticles() {
        super(WrappedPacket.particalPacket);
    }

    public WrappedPacketPlayOutWorldParticles(EnumParticle var1, boolean var2, float var3, float var4, float var5, float var6, float var7, float var8, float var9, int var10, int... var11) {
        super(WrappedPacket.particalPacket);
        this.a = var1.value;
        this.j = var2;
        this.b = var3;
        this.c = var4;
        this.d = var5;
        this.e = var6;
        this.f = var7;
        this.g = var8;
        this.h = var9;
        this.i = var10;
        this.k = var11;
    }

    public void setA(EnumParticle a) {
        this.a = a.value;
    }

    public EnumParticle getA() {
        return EnumParticle.get(a);
    }

    public enum EnumParticle {
        EXPLOSION_NORMAL("explode", 0),
        EXPLOSION_LARGE("largeexplode", 1),
        EXPLOSION_HUGE("hugeexplosion", 2),
        FIREWORKS_SPARK("fireworksSpark", 3),
        WATER_BUBBLE("bubble", 4),
        WATER_SPLASH("splash", 5),
        WATER_WAKE("wake", 6),
        SUSPENDED("suspended", 7),
        SUSPENDED_DEPTH("depthsuspend", 8),
        CRIT("crit", 9),
        CRIT_MAGIC("magicCrit", 10),
        SMOKE_NORMAL("smoke", 11),
        SMOKE_LARGE("largesmoke", 12),
        SPELL("spell", 13),
        SPELL_INSTANT("instantSpell", 14),
        SPELL_MOB("mobSpell", 15),
        SPELL_MOB_AMBIENT("mobSpellAmbient", 16),
        SPELL_WITCH("witchMagic", 17),
        DRIP_WATER("dripWater", 18),
        DRIP_LAVA("dripLava", 19),
        VILLAGER_ANGRY("angryVillager", 20),
        VILLAGER_HAPPY("happyVillager", 21),
        TOWN_AURA("townaura", 22),
        NOTE("note", 23),
        PORTAL("portal", 24),
        ENCHANTMENT_TABLE("enchantmenttable", 25),
        FLAME("flame", 26),
        LAVA("lava", 27),
        FOOTSTEP("footstep", 28),
        CLOUD("cloud", 29),
        REDSTONE("reddust", 30),
        SNOWBALL("snowballpoof", 31),
        SNOW_SHOVEL("snowshovel", 32),
        SLIME("slime", 33),
        HEART("heart", 34),
        BARRIER("barrier", 35),
        ITEM_CRACK("iconcrack_", 36),
        BLOCK_CRACK("blockcrack_", 37),
        BLOCK_DUST("blockdust_", 38),
        WATER_DROP("droplet", 39),
        ITEM_TAKE("take", 40),
        MOB_APPEARANCE("mobappearance", 41);

        private String name;
        private int id;
        private Object value;

        EnumParticle(String name, int id) {
            this.name = name;
            this.id = id;
            this.value = calcValue();
        }

        private Object calcValue() {
            for (Object e : enumParticle.getEnumConstants()) {
                String ename = Reflective.get(e,particalName);
                if (name.equalsIgnoreCase(ename))
                    return e;
            }
            System.err.println("Could not find ENUM_PARTICLE: "+name);
            return enumParticle.getEnumConstants()[0];
        }

        private static EnumParticle get(Object o) {
            for (EnumParticle v : values())
                if (v == o)
                    return v;
            return CRIT;
        }

    }

}
