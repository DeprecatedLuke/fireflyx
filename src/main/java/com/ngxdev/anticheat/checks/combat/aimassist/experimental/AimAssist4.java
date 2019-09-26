package com.ngxdev.anticheat.checks.combat.aimassist.experimental;

import com.ngxdev.anticheat.api.check.Check;
import api.CheckType;
import com.ngxdev.tinyprotocol.packet.in.WrappedInFlyingPacket;

import static api.CheckType.State.EXPERIMENTAL;
import static api.CheckType.Type.COMBAT;

@CheckType(id = "aimassist:4", name = "AimAssist 4", type = COMBAT, state = EXPERIMENTAL)
public class AimAssist4 extends Check {
	void check(WrappedInFlyingPacket packet) {
		if (!canCheck()
				|| data.timers.lastAttack.hasPassed(20 * 10)
				|| !packet.isLook()) return;

		float yawDifference = data.movement.yawDifference;

		//Rounded yaw
		if (yawDifference > 0 && Math.abs(Math.floor(yawDifference) - yawDifference) < 0.0000000001) {
			fail(3, 20 * 5);
		}
	}
}
