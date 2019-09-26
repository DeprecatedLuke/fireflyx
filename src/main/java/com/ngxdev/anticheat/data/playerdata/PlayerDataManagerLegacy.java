package com.ngxdev.anticheat.data.playerdata;

import org.bukkit.entity.Player;

import java.util.Map;
import java.util.UUID;
import java.util.WeakHashMap;
import java.util.concurrent.ConcurrentHashMap;

public class PlayerDataManagerLegacy implements PlayerDataManager {
	private Map<UUID, PlayerData> players = new ConcurrentHashMap<>();

	public PlayerData _get(Player player) {
		return players.get(player.getUniqueId());
	}

	public void _set(Player player, PlayerData data) {
		players.put(player.getUniqueId(), data);
	}

	public void _remove(Player player) {
		players.remove(player.getUniqueId());
	}
}
