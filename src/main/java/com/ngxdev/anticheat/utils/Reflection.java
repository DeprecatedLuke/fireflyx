package com.ngxdev.anticheat.utils;

import com.ngxdev.tinyprotocol.reflection.FieldAccessor;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

public class Reflection {
    public static String getServerVersion() {
        String packageName = Bukkit.getServer().getClass().getPackage().getName();
        return packageName.substring(packageName.lastIndexOf('.') + 1);
    }

    private static final FieldAccessor<Integer> accessor = com.ngxdev.tinyprotocol.reflection.Reflection.getField("{nms}.Entity", "entityCount", int.class);

    public static int incrementEntityCount() throws Exception {
        int count = accessor.get(null);
        accessor.set(null, count + 1);
        return count;
    }

    public static int getMinorVersion() {
        return Integer.parseInt(getServerVersion().split("_")[1]);
    }

    public static boolean set(final Object o1, final String field, final Object o2) {
        try {
            final Field f = getField(o1.getClass(), field);
            f.setAccessible(true);
            f.set(o1, o2);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public static <T> T get(final Object o, final String field) {
        try {
            final Field f = getField(o.getClass(), field);
            f.setAccessible(true);
            return (T) f.get(o);
        } catch (Throwable e) {
            e.printStackTrace();
        }
        return null;
    }

    public static <T> T getMute(final Object o, final String field) {
        try {
            final Field f = getField(o.getClass(), field);
            f.setAccessible(true);
            return (T) f.get(o);
        } catch (Throwable e) {

        }
        return null;
    }


    private static Field getField(final Class c, final String name) throws NoSuchFieldException {
        try {
            return c.getDeclaredField(name);
        } catch (NoSuchFieldException e) {
            if (c != Object.class) {
                final Class _super = c.getSuperclass();
                return getField(_super, name);
            }
            throw e;
        }
    }

    public static boolean setFinal(final Object o1, final String field, final Object o2) {
        try {
            final Field f = getField(o1.getClass(), field);
            f.setAccessible(true);
            Field modifiersField = Field.class.getDeclaredField("modifiers");
            modifiersField.setAccessible(true);
            modifiersField.setInt(f, f.getModifiers() & ~Modifier.FINAL);
            f.set(o1, o2);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean set(final Object o1, Field field, final Object o2) {
        try {
            field.setAccessible(true);
            field.set(o1, o2);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public static <T> T get(final Object o, final Field field) {
        try {
            field.setAccessible(true);
            return (T) field.get(o);
        } catch (Throwable e) {
            e.printStackTrace();
        }
        return null;
    }

    public static boolean setFinal(final Object o1, final Field field, final Object o2) {
        try {
            field.setAccessible(true);
            Field modifiersField = Field.class.getDeclaredField("modifiers");
            modifiersField.setAccessible(true);
            modifiersField.setInt(field, field.getModifiers() & ~Modifier.FINAL);
            field.set(o1, o2);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public static Field findField(Class parent, Class fieldType) {
        while (parent != Object.class) {
            Field[] fields = parent.getDeclaredFields();
            for (Field field : fields) {
                if (field.getType().isAssignableFrom(fieldType)) {
                    field.setAccessible(true);
                    return field;
                }
            }
            parent = parent.getSuperclass();
        }
        return null;
    }

    public static Field findField(Class parent, Class fieldType, int index) {
        while (parent != Object.class) {
            Field[] fields = parent.getDeclaredFields();
            for (Field field : fields) {
                if (field.getType().isAssignableFrom(fieldType)) {
                    if (index == 0) {
                        field.setAccessible(true);
                        return field;
                    } else index--;
                }
            }
            parent = parent.getSuperclass();
        }
        return null;
    }


    public static <T> T newInstance(Class clazz) {
        try {
            Constructor e = clazz.getConstructor((Class<?>[]) new Class[0]);
            e.setAccessible(true);
            return (T) e.newInstance();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
