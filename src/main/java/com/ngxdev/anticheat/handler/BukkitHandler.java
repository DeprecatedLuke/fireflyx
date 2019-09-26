/*
 * Copyright (c) 2018 NGXDEV.COM. Licensed under MIT.
 */

package com.ngxdev.anticheat.handler;

import com.ngxdev.anticheat.Firefly;
import com.ngxdev.anticheat.data.playerdata.PlayerData;
import com.ngxdev.anticheat.data.playerdata.PlayerDataManager;
import com.ngxdev.anticheat.utils.Init;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scheduler.BukkitTask;

@Init
public class BukkitHandler implements Listener {
	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onEvent(PlayerJoinEvent e) {
		PlayerData data;
		try {
			data = PlayerData.get(e.getPlayer());
		} catch (Exception er) {
			e.getPlayer().kickPlayer("Server is still starting...");
			return;
		}

		Bukkit.getScheduler().scheduleSyncDelayedTask(Firefly.getInstance(), () -> {
			PlayerDataManager.set(e.getPlayer(), data);
		}, 1);
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void onEvent(PlayerQuitEvent e) {
		PlayerData data = PlayerData.getOrNull(e.getPlayer());

		if (data == null) {
			System.out.println("[FireFlyX] [WARN] Failed to cleanup PlayerData");
			return;
		}
		data.methods.clear();
		data.validChecks.clear();
		data.allChecks.clear();
		data.scheduledTasks.values().forEach(BukkitTask::cancel);
		data.scheduledTasks.clear();
		PlayerDataManager.remove(e.getPlayer());
	}
}
