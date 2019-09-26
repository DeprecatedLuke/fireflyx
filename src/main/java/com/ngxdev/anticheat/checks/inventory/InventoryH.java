package com.ngxdev.anticheat.checks.inventory;

import api.CheckType;
import com.ngxdev.anticheat.api.check.Check;
import com.ngxdev.tinyprotocol.packet.in.WrappedInFlyingPacket;
import com.ngxdev.tinyprotocol.packet.in.WrappedInWindowClickPacket;
import org.bukkit.Material;

import static api.CheckType.Type.INVENTORY;

@CheckType(id = "inventory:h", name = "Inventory H", type = INVENTORY)
public class InventoryH extends Check {
	void check(WrappedInWindowClickPacket packet) {
		if (!canCheckMovement()) return;
		WrappedInWindowClickPacket.ClickType c = packet.getAction();

		if ((packet.getItem() != null || c.isShiftClick())
				&& data.movement.deltaH > data.movement.lastDeltaH
				&& data.timers.lastVelocitySent.hasPassed(data.getEstimatedPacketLag() + 10)) {
			if (fail(6, 20 * 5, "C")) {
				player.closeInventory();
			}
		}

		if (packet.getItem() != null) {
			if (player.getLocation().getBlock().getType() == Material.PORTAL || player.getLocation().add(0, 1, 0).getBlock().getType() == Material.PORTAL) {
				if (fail(6, 20 * 5, "P")) {
					player.closeInventory();
				}
			}
		}
	}

	void check(WrappedInFlyingPacket packet) {
		if (!canCheckMovement()) return;

		if (data.movement.deltaH > data.movement.lastDeltaH
				&& data.timers.lastVelocitySent.hasPassed(data.getEstimatedPacketLag() + 10)
				&& isUsingTheCursor()) {
			if (fail(6, 20 * 5, "M")) {
				player.closeInventory();
			}
		}
	}

	public boolean isUsingTheCursor() {
		return player != null && player.getOpenInventory().getCursor().getType() != Material.AIR;
	}
}
