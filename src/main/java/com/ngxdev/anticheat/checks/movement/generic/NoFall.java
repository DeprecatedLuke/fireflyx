package com.ngxdev.anticheat.checks.movement.generic;

import com.ngxdev.anticheat.api.check.Check;
import api.CheckType;
import com.ngxdev.anticheat.api.events.PlayerTakeVelocityEvent;
import com.ngxdev.tinyprotocol.packet.in.WrappedInFlyingPacket;

import static api.CheckType.Type.MOVEMENT;

@CheckType(id = "nofall", name = "NoFall", type = MOVEMENT)
public class NoFall extends Check {
	float fallDistance;
	float lastFallDistance;
	int airTicks;

	void check(PlayerTakeVelocityEvent event) {
		fallDistance = 0;
		lastFallDistance = 0;
	}

	void check(WrappedInFlyingPacket packet) {
		if (!canCheck()
				|| !isMoving()
				|| !packet.isPos()
				|| data.enviorment.inLiquid.hasNotPassed(5)
				|| data.velocity.deltaV != 0
				|| isGliding()) return;
		double d = data.movement.ty - data.movement.fy;
		boolean allow = true;


		if (data.enviorment.onGround.wasReset()) {
			allow = false;
			fallDistance = 0;
		}

		if (packet.isGround()) airTicks = 0;
		else airTicks++;

		if (d < 0) fallDistance = (float) (fallDistance - d);

		if (allow && d <= -0.1) {
			float fall = fallDistance - lastFallDistance;

			if ((int) data.movement.ty < (int) data.movement.fy) {

				float fd = fallDistance;
				double value = fd - Math.floor(fd);

				if (fall <= 0.0f && value <= 0.6
						&& (airTicks >= 8 || d <= -0.5)
						&& data.timers.lastVelocity.hasPassed(20)) {
					fail("f=%.2f,v=%.2f,a=%d,d=%.2f", fall, value, airTicks, d);
				}
			}
		}
		lastFallDistance = fallDistance;
	}
}
