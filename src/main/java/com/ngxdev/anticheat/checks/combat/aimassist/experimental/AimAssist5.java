package com.ngxdev.anticheat.checks.combat.aimassist.experimental;

import com.ngxdev.anticheat.api.check.Check;
import api.CheckType;
import com.ngxdev.tinyprotocol.packet.in.WrappedInFlyingPacket;

import static api.CheckType.State.EXPERIMENTAL;
import static api.CheckType.Type.COMBAT;

@CheckType(id = "aimassist:5", name = "AimAssist 5", type = COMBAT, state = EXPERIMENTAL)
public class AimAssist5 extends Check {
	void check(WrappedInFlyingPacket packet) {
		if (!canCheck()
				|| data.timers.lastAttack.hasPassed(20 * 10)
				|| !packet.isLook()) return;

		float pitchChange = data.movement.deltaPitch;
		float yawChange = data.movement.deltaYaw;
		float pitchDifference = data.movement.pitchDifference;

		float yawDifference = data.movement.yawDifference;

		//Extremely randomized
		if (yawChange > yawDifference && yawDifference > 0.0 && pitchChange > 0 && pitchChange < 0.02 && pitchDifference > pitchChange * 2) {
			fail(1, 20 * 5);
		}
	}
}
