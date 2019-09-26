package com.ngxdev.anticheat.utils;

import com.ngxdev.tinyprotocol.api.NMSObject;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class PreDefined {
    public static Constructor<?> dataWatcher;
    public static Method asCraftMirror, dataWatcherAdd, dataWatcherAddNew;

    public static Object emptyDatawatcher;
    public static Object invisibleEntity;
    public static List emptyEntityWatchables = new ArrayList();
    public static List invisibleEntityWatchables = new ArrayList();

    static {
        try {
            asCraftMirror = Class.forName(NMSObject.Type.CRAFTITEMSTACK).getMethod("asCraftMirror", Class.forName(NMSObject.Type.ITEMSTACK));
            Class<?> watcher = Class.forName(NMSObject.Type.DATAWATCHER);

            dataWatcher = watcher.getConstructor(Class.forName(NMSObject.Type.ENTITY));

            try {
                dataWatcherAdd = watcher.getMethod("a", int.class, Object.class);
                if (dataWatcherAdd == null) throw new NullPointerException();
            } catch (NullPointerException | NoSuchMethodException e) {
                Class<?> watcherObject = Class.forName(NMSObject.Type.DATAWATCHEROBJECT);
                dataWatcherAddNew = watcher.getMethod("register", watcherObject, Object.class);
            }

            if (!Reflection.getServerVersion().startsWith("v1_7") && !Reflection.getServerVersion().startsWith("v1_8_R1")) {
                Class<?> watchable = com.ngxdev.tinyprotocol.reflection.Reflection.getMinecraftClass("DataWatcher$WatchableObject");
                Constructor c = watchable.getConstructor(int.class, int.class, Object.class);
                invisibleEntityWatchables.add(c.newInstance(0, 0, (byte) 0x20));
            }
            emptyDatawatcher = dataWatcher.newInstance((Object) null);
            invisibleEntity = dataWatcher.newInstance((Object) null);
            if (Reflection.getServerVersion().startsWith("v1_9") || Reflection.getServerVersion().startsWith("v1_11") || Reflection.getServerVersion().startsWith("v1_12")) {
                //Packet.dataWatcherAddNew.invoke(emptyDatawatcher, )
            } else {
                dataWatcherAdd.invoke(emptyDatawatcher, 0, (byte) 0x00);
                dataWatcherAdd.invoke(invisibleEntity, 0, (byte) 0x20);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static Object getNewDatawatcherObject(int index, int type, Object value) {
        return null;
    }
}