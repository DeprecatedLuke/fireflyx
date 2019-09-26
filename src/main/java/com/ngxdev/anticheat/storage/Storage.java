package com.ngxdev.anticheat.storage;

import api.CheckWrapper;
import api.ViolationX;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public interface Storage {
    void init();

    default double getInt(String key, int def) {
        String value = getString(key);
        if (value == null) return def;
        return Integer.parseInt(value);
    }

    default double getDouble(String key, double def) {
        String value = getString(key);
        if (value == null) return def;
        return Double.parseDouble(value);
    }

    default boolean getBoolean(String key, boolean def) {
        String value = getString(key);
        if (value == null) return def;
        return Boolean.parseBoolean(value);
    }

    default String getString(String key, String def) {
        String value = getString(key);
        if (value == null) return def;
        return value;
    }

    String getString(String key);

    void set(String key, Object value);

    void updateValue(CheckWrapper type);

    void addAlert(ViolationX violation);

    List<ViolationX> getViolations(UUID uuid, CheckWrapper type, int page, int limit, long from, long to);

    Map<CheckWrapper, Integer> getHighestViolations(UUID uuid, CheckWrapper type, long from, long to);
}
