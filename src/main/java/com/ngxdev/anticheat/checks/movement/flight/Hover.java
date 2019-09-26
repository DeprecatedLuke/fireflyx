package com.ngxdev.anticheat.checks.movement.flight;

import api.CheckType;
import com.ngxdev.anticheat.api.check.Check;
import com.ngxdev.anticheat.api.check.Priority;
import com.ngxdev.tinyprotocol.packet.in.WrappedInFlyingPacket;

import static api.CheckType.State.BETA;
import static api.CheckType.Type.MOVEMENT;
import static com.ngxdev.anticheat.api.check.Priority.Value.HIGHER;

@CheckType(id = "hover", name = "Hover", type = MOVEMENT, state = BETA)
public class Hover extends Check {
	private int hoverFails = 0;

//	@Priority(HIGHER)
//	void parse(WrappedInFlyingPacket packet) {
//		if (!packet.isPos()
//				|| data.timers.lastTeleport.hasNotPassed(2)
//				|| data.timers.lastTeleportSent.hasNotPassed(2)
//				|| data.enviorment.inLiquid.wasReset()
//				|| data.enviorment.onLadder.hasNotPassed(2)
//				|| data.timers.lastBlockGlitch.hasNotPassed(5)
//				|| data.timers.slimePush.hasNotPassed(10)
//				|| data.velocity.deltaH != 0
//				|| data.velocity.deltaV != 0
//				|| data.timers.lastVelocitySent.hasNotPassed(10)
//				|| data.timers.lastFlightToggle.hasNotPassed(10)
//				|| isGliding()
//				|| data.movement.deltaV != 0
//				|| data.enviorment.onGround.wasReset()) {
//			data.timers.lastHover.reset();
//		}
//	}

	void check(WrappedInFlyingPacket packet) {
		if (!packet.isPos()
				|| !canCheckMovement()
				|| (isAccurateOnGround(packet) && data.enviorment.inLiquid.wasNotReset())
				|| (isAccurateOnGround(packet) && data.enviorment.inLiquid.wasReset())
				|| data.timers.lastFlightToggle.hasNotPassed(10)
				|| data.timers.lastBlockGlitch.hasNotPassed(5)
				|| data.enviorment.onLadder.hasNotPassed(2)
				|| data.velocity.deltaV != 0
				|| data.movement.deltaV != 0) {
			debug("RESET");
			hoverFails = 0;
			return;
		}

		if (hoverFails++ == 6) {
			hoverFails = 0;
			if (data.timers.join.hasPassed(20)) {
				if (fail()) setback(1);
			}
		}
		debug("%s | V: %s", hoverFails, data.movement.deltaV);
	}
}
