package com.ngxdev.anticheat.checks.movement.generic;

import com.ngxdev.anticheat.api.check.Check;
import com.ngxdev.anticheat.api.check.Priority;
import api.CheckType;
import com.ngxdev.anticheat.utils.world.BlockData;
import org.bukkit.event.block.BlockPlaceEvent;

import static com.ngxdev.anticheat.api.check.Priority.Value.HIGHER;
import static api.CheckType.Type.GAMEPLAY;

@CheckType(id = "generic:blockglitch", name = "Block Glitch", type = GAMEPLAY, alert = false)
public class BlockGlitch extends Check {
	@Priority(HIGHER)
	void check(BlockPlaceEvent event) {
		if (!event.isCancelled()
				|| !canCheckMovement()) return;

		data.enviorment.handler.setOffset(-0.15);
		if (Math.abs(data.movement.ty - (event.getBlock().getY() + 1)) < 0.15
				&& data.enviorment.handler
				.isCollidedWith(BlockData.getData(event.getBlock().getType())
						.getBox(event.getBlock(), data.protocolVersion))) {
			data.timers.lastBlockGlitch.reset();
			if (fail()) setback(-1);
		}
		data.enviorment.handler.setOffset(0);
	}
}
