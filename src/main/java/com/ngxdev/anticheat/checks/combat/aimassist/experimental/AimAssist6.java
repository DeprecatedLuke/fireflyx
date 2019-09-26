package com.ngxdev.anticheat.checks.combat.aimassist.experimental;

import com.ngxdev.anticheat.api.check.Check;
import api.CheckType;
import com.ngxdev.tinyprotocol.packet.in.WrappedInFlyingPacket;

import static api.CheckType.State.EXPERIMENTAL;
import static api.CheckType.Type.COMBAT;

@CheckType(id = "aimassist:6", name = "AimAssist 6", type = COMBAT, state = EXPERIMENTAL)
public class AimAssist6 extends Check {
	void check(WrappedInFlyingPacket packet) {
		if (!canCheck()
				|| data.timers.lastAttack.hasPassed(20 * 10)
				|| !packet.isLook()) return;

		float pitchChange = data.movement.deltaPitch;
		float pitchDifference = data.movement.pitchDifference;

		float yawDifference = data.movement.yawDifference;

		//Turning aim assist on and off
		if (yawDifference > 900 && pitchChange > 0 && pitchDifference < 10) {
			fail();
		}
	}
}
