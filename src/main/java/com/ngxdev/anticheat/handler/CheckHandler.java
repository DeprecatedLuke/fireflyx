/*
 * Copyright (c) 2018 NGXDEV.COM. Licensed under MIT.
 */

package com.ngxdev.anticheat.handler;

import api.CheckWrapper;
import com.ngxdev.anticheat.api.check.Check;
import com.ngxdev.anticheat.api.check.MethodWrapper;
import api.CheckType;
import com.ngxdev.anticheat.api.check.type.Parser;
import com.ngxdev.anticheat.data.playerdata.PlayerData;

import java.util.*;
import java.util.stream.Collectors;


@SuppressWarnings("unchecked")
public class CheckHandler {
    private static List<Class<? extends Check>> checks = new ArrayList<>();
    private static List<Class<? extends Check>> parsers = new ArrayList<>();
    private static Map<Class<? extends Check>, CheckWrapper> wrappers = new HashMap<>();

    public static void put(CheckWrapper wrapper) {
        Class<? extends Check> clazz = getClass(wrapper.id());
        if (clazz == null) return;
        CheckHandler.wrappers.put(clazz, wrapper);
        PlayerData.getAll().forEach(data -> {
            for (Check c : data.validChecks) {
                CheckWrapper cw = wrappers.get(c.getClass());
                if (cw != null) c.check = cw;
            }
        });
    }

    public static Map<Class<? extends Check>, CheckWrapper> getWrappers() {
        return wrappers;
    }

    public static Class<? extends Check> getClass(String id) {
        for (Map.Entry<Class<? extends Check>, CheckWrapper> e : wrappers.entrySet()) if (e.getValue().id().equalsIgnoreCase(id)) return e.getKey();
        return null;
    }

    public static String getName(String id) {
        for (Map.Entry<Class<? extends Check>, CheckWrapper> e : wrappers.entrySet()) if (e.getValue().id().equalsIgnoreCase(id)) return e.getKey().getAnnotation(CheckType.class).name();
        return "[Removed]";
    }

    public static CheckWrapper getWrapper(String id) {
        for (CheckWrapper wrapper : wrappers.values()) if (wrapper.id().equalsIgnoreCase(id)) return wrapper;
        return null;
    }

    public static List<CheckWrapper> getWrapper(CheckType.Type type) {
        List<CheckWrapper> wrappers = new ArrayList<>();
        for (Map.Entry<Class<? extends Check>, CheckWrapper> e : CheckHandler.wrappers.entrySet()) if (e.getKey().getAnnotation(CheckType.class).type() == type) wrappers.add(e.getValue());
        return wrappers.stream().sorted((p1, p2) -> compareToSpecial(p1.id(), p2.id())).collect(Collectors.toList());
    }

    public static int compareToSpecial(String p1, String p2) {
        if (Character.isDigit(p1.toCharArray()[p1.length() -1]) && Character.isDigit(p2.toCharArray()[p2.length() -1])) return p1.compareTo(p2);
        else if (Character.isDigit(p1.toCharArray()[p1.length() -1]) && !Character.isDigit(p2.toCharArray()[p2.length() -1])) return 1;
        else if (!Character.isDigit(p1.toCharArray()[p1.length() -1]) && Character.isDigit(p2.toCharArray()[p2.length() -1])) return -1;
        else return p1.compareTo(p2);
    }

    public static CheckType.Type getType(String id) {
        for (Map.Entry<Class<? extends Check>, CheckWrapper> e : wrappers.entrySet()) if (e.getValue().id().equalsIgnoreCase(id)) return e.getKey().getAnnotation(CheckType.class).type();
        return null;
    }

    public CheckHandler() {
        try {
            CheckType.Dynamic.get().forEach(clazz -> {
                try {
                    checks.add((Class<? extends Check>) clazz);
                    wrappers.put((Class<? extends Check>) clazz, new CheckWrapper((Class<? extends Check>) clazz));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });

            Parser.Dynamic.get().forEach(clazz -> {
                try {
                    parsers.add((Class<? extends Check>) clazz);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
        } catch (Throwable e) {
            e.printStackTrace();
        }
        System.out.println("[FireFlyX] Registered " + (checks.size() + parsers.size()) + " checks...");
    }

    public static void init(PlayerData data) {
        checks.forEach(c -> {
            try {
                Check check = c.newInstance();
                check.init(data, wrappers.computeIfAbsent(c, CheckWrapper::new));
                data.allChecks.add(check);
                if (check.getId() != null) data.validChecks.add(check);
                else System.err.println("Missing annotation: " + check.getClass().getSimpleName());
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        parsers.forEach(c -> {
            try {
                Check check = c.newInstance();
                check.init(data, null);
                data.allChecks.add(check);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        data.methods.sort(Comparator.comparingInt(MethodWrapper::getPriority));
        data.validChecks.sort(Comparator.comparing(Check::getLowerName));
    }
}
