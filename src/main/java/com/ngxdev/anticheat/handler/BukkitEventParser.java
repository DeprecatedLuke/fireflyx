package com.ngxdev.anticheat.handler;

import com.ngxdev.anticheat.api.HumanNPC;
import com.ngxdev.anticheat.data.playerdata.PlayerData;
import com.ngxdev.anticheat.utils.Init;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPistonExtendEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.*;

// This is the fastest...
@Init
public class BukkitEventParser implements Listener {
	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onEvent(BlockPistonExtendEvent e) {
		for (Player player : e.getBlock().getWorld().getPlayers()) {
			if (e.getBlock().getLocation().distance(player.getLocation()) < 10) {
				for (Block block : e.getBlocks()) {
					if (block.getType() == Material.SLIME_BLOCK) {
						if (player.getLocation().distance(block.getLocation()) < 2.5) {
							PlayerData.get(player).timers.slimePush.reset();
						}
					}
				}
			}
		}
	}

	@EventHandler(priority = EventPriority.MONITOR)
	void onEvent(PlayerInteractEvent e) {
		if (isNotReal(e.getPlayer())) return;
		PlayerData.get(e.getPlayer()).fireChecks(e);
	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	void onEvent(PlayerGameModeChangeEvent e) {
		if (isNotReal(e.getPlayer())) return;
		PlayerData data = PlayerData.get(e.getPlayer());

		data.gamemode = e.getNewGameMode();
		data.fireChecks(e);
	}

	@EventHandler(priority = EventPriority.MONITOR)
	void onEvent(PlayerToggleFlightEvent e) {
		if (isNotReal(e.getPlayer())) return;
		PlayerData.get(e.getPlayer()).fireChecks(e);
	}

	@EventHandler(priority = EventPriority.MONITOR)
	void onEvent(BlockPlaceEvent e) {
		if (isNotReal(e.getPlayer())) return;
		PlayerData.get(e.getPlayer()).fireChecks(e);
	}

	@EventHandler(priority = EventPriority.MONITOR)
	void onEvent(EntityDamageEvent e) {
		if (e.getEntity() instanceof Player) {
			if (isNotReal((Player) e.getEntity())) return;
			PlayerData.get((Player) e.getEntity()).fireChecks(e);
		}
	}

	@EventHandler(priority = EventPriority.MONITOR)
	void onEvent(EntityDamageByEntityEvent e) {
		if (e.getDamager() instanceof Player) {
			if (isNotReal((Player) e.getDamager())) return;
			PlayerData.get((Player) e.getDamager()).fireChecks(e);
		}
	}

	@EventHandler(priority = EventPriority.MONITOR)
	void onEvent(PlayerItemHeldEvent e) {
		if (isNotReal(e.getPlayer())) return;
		PlayerData.get(e.getPlayer()).fireChecks(e);
	}

	@EventHandler(priority = EventPriority.MONITOR)
	void onEvent(PlayerToggleSneakEvent e) {

	}

	@EventHandler(priority = EventPriority.MONITOR)
	void onEvent(EntityShootBowEvent e) {
		if (e.getEntity() instanceof Player) {
			if (isNotReal((Player) e.getEntity())) return;
			PlayerData.get((Player) e.getEntity()).fireChecks(e);
		}
	}

	@EventHandler(priority = EventPriority.MONITOR)
	void onEvent(PlayerRespawnEvent e) {
		if (isNotReal(e.getPlayer())) return;
		PlayerData.get(e.getPlayer()).fireChecks(e);
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	void onEvent(PlayerTeleportEvent e) {
		if (isNotReal(e.getPlayer())) return;
		PlayerData data = PlayerData.get(e.getPlayer());
		data.fireChecks(e);

		for (HumanNPC npc : data.npc.npcs) {
			npc.destroyEntity(data);
		}
		data.npc.npcs.clear();
	}

	@EventHandler(priority = EventPriority.MONITOR)
	void onEvent(InventoryCloseEvent e) {
		if (isNotReal((Player) e.getPlayer())) return;
		PlayerData data = PlayerData.get((Player) e.getPlayer());
		data.state.isInventoryOpen = false;
	}

	@EventHandler(priority = EventPriority.MONITOR)
	void onEvent(PlayerMoveEvent e) {
		if (isNotReal(e.getPlayer())) return;
		PlayerData data = PlayerData.get(e.getPlayer());
		data.fireChecks(e);
	}

	private boolean isNotReal(Player player) {
		if (PlayerData.getOrNull(player) != null) return false;
		return !TinyProtocolHandler.instance.hasInjected(player);
	}
}
