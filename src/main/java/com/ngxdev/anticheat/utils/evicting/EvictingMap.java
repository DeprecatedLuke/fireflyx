/*
 * Copyright (c) 2018 NGXDEV.COM. Licensed under MIT.
 */

package com.ngxdev.anticheat.utils.evicting;

import java.util.TreeMap;

public class EvictingMap<K, V> extends TreeMap<K, V> {
    private int maxSize;

    public EvictingMap(int maxSize) {
        this.maxSize = maxSize;
    }

    @Override
    public V put(K key, V value) {
        if (size() >= maxSize) remove(firstKey());
        return super.put(key, value);
    }
}
