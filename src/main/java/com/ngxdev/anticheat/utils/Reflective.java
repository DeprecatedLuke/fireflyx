package com.ngxdev.anticheat.utils;

import com.ngxdev.anticheat.utils.exception.ExceptionLog;
import lombok.SneakyThrows;
import org.bukkit.Bukkit;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

public class Reflective {

    public static final String VERSION = Bukkit.getServer().getClass().getName().split("\\.")[3];

    @SneakyThrows
    public static <T> Class<T> c(String s) {
        return (Class<T>) Class.forName(s,true,Reflective.class.getClassLoader());
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

    public static Field access(final Class<?> o, final String field) {
        try {
            final Field f = getField(o, field);
            f.setAccessible(true);
            return f;
        } catch (Throwable e) {
            e.printStackTrace();
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

    @SneakyThrows
    public static <T> T call(Object o, String method, Object... param) {
        Method m = getMethod(o,method);
        m.setAccessible(true);
        return (T) m.invoke(o,param);
    }

    public static Method getMethod(final Object o, final String field) {
        try {
            return _getMethod(o.getClass(), field);
        } catch (Throwable e) {
            e.printStackTrace();
        }
        return null;
    }

    @SneakyThrows
    public static Method _getMethod(final Class c, final String name) {
        try {
            Method[] methods = c.getDeclaredMethods();
            for (Method m : methods)
                if (m.getName().equals(name))
                    return m;
            throw new NoSuchMethodException("Could not find method: "+name);
        } catch (NoSuchMethodException e) {
            if (c != Object.class) {
                final Class _super = c.getSuperclass();
                return _getMethod(_super, name);
            }
            throw e;
        }
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


    public static <T> T newInstance(Class clazz) {
        try {
            Constructor e = clazz.getConstructor((Class<?>[]) new Class[0]);
            e.setAccessible(true);
            return (T) e.newInstance();
        } catch (Exception e) {
            ExceptionLog.log(e);
            return null;
        }
    }

}