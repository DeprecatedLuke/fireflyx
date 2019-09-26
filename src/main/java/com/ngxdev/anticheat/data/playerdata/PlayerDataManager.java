package com.ngxdev.anticheat.data.playerdata;

import org.bukkit.entity.Player;

public interface PlayerDataManager {
    PlayerDataManager instance = getInstance();

    static PlayerData get(Player player) {
        return instance._get(player);
    }
    static void set(Player player, PlayerData data) {
        instance._set(player, data);
    }
    static void remove(Player player) { instance._remove(player); }

    PlayerData _get(Player player);

    void _set(Player player, PlayerData data);

    void _remove(Player player);

    static PlayerDataManager getInstance() {
        if (instance != null) {
            return instance;
        }
        return new PlayerDataManagerLegacy();
//        try {
//            return (PlayerDataManager) Reflective.c(PlayerDataManagerModern.class.getName()).newInstance();
//        } catch (Exception e) {
//            System.err.println("Failed to instantiate PlayerDataManagerModern, falling back to legacy system. (ignore this message if you are using 1.7)");
//            return new PlayerDataManagerLegacy();
//        }
    }

}
