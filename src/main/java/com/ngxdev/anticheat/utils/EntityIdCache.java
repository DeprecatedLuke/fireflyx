package com.ngxdev.anticheat.utils;

public class EntityIdCache {
    private static int[] idCache = new int[10000];
    private static int nextId = 0;

    static {
        try {
            for (int i = 0; i < idCache.length; i++) {
                idCache[i] = Reflection.incrementEntityCount();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static int getNextId() {
        nextId++;
        if (nextId >= 10000) nextId = 0;
        return idCache[nextId];
    }
}
