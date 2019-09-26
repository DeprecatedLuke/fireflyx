package com.ngxdev.anticheat.data.playerdata;

import com.mojang.authlib.GameProfile;
import com.ngxdev.anticheat.utils.Reflective;
import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;
import org.bukkit.entity.Player;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;

public class PlayerDataManagerModern implements PlayerDataManager {

    private static Method handle = Reflective._getMethod(Reflective.c("org.bukkit.craftbukkit."+Reflective.VERSION+".entity.CraftPlayer"),"getHandle");
    private static Field gameProfile = Reflective.findField(Reflective.c("net.minecraft.server."+Reflective.VERSION+".EntityHuman"),GameProfile.class);

    public PlayerData _get(Player player) {
        return getOrInject(player).data;
    }

    public void _set(Player player, PlayerData data) {
        getOrInject(player).data = data;
    }

    @Override
    public void _remove(Player player) {
        getOrInject(player).data = null;
    }

    @SneakyThrows
    private static PlayerDataReferenceInjection getOrInject(Player player) {
        Object o = handle.invoke(player);
        GameProfile profile = (GameProfile) gameProfile.get(o);
        if (profile instanceof PlayerDataReferenceInjection) return (PlayerDataReferenceInjection) profile;
        PlayerDataReferenceInjection referenceInjection = new PlayerDataReferenceInjection(profile);
        gameProfile.set(o, referenceInjection);
        return referenceInjection;
    }

    private static class PlayerDataReferenceInjection extends GameProfile {

        @Getter @Setter
        private PlayerData data; // injected field

        private static List<Field> fields = fields(new ArrayList<>());

        @SneakyThrows
        public PlayerDataReferenceInjection(GameProfile profile) {
            super(profile.getId(),profile.getName());
            for (Field f : fields)
                f.set(this,f.get(profile));
        }

        private static List<Field> fields(List<Field> list) {
            for (Field f : GameProfile.class.getDeclaredFields()) {
                if (!Modifier.isStatic(f.getModifiers())) {
                    UnFinal(f);
                    f.setAccessible(true);
                    list.add(f);
                }
            }
            return list;
        }

        @SneakyThrows
        private static void UnFinal(Field field) {
            if (Modifier.isFinal(field.getModifiers())) {
                Field modifiersField = Field.class.getDeclaredField("modifiers");
                modifiersField.setAccessible(true);
                modifiersField.setInt(field, field.getModifiers() & ~Modifier.FINAL);
            }
        }

    }

}
