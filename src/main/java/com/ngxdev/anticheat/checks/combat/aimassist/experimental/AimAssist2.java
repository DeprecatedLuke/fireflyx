package com.ngxdev.anticheat.checks.combat.aimassist.experimental;

import com.ngxdev.anticheat.api.check.Check;
import api.CheckType;
import com.ngxdev.tinyprotocol.packet.in.WrappedInFlyingPacket;

import static api.CheckType.State.EXPERIMENTAL;
import static api.CheckType.Type.COMBAT;

@CheckType(id = "aimassist:2", name = "AimAssist 2", type = COMBAT, state = EXPERIMENTAL, maxVl = 6, timeout = 20 * 60 * 10)
public class AimAssist2 extends Check {
	private float lastPitchRate;
	private float lastYawRate;

	void check(WrappedInFlyingPacket packet) {
		if (!canCheck()
				|| data.timers.lastAttack.hasPassed(20 * 10)
				|| !packet.isLook()) return;

		float diffPitch = data.movement.deltaPitch;
		float diffYaw = data.movement.deltaYaw;

		float diffYawPitch = Math.abs(diffYaw - diffPitch);

		float diffPitchRate = Math.abs(this.lastPitchRate - diffPitch);
		float diffYawRate = Math.abs(this.lastYawRate - diffYaw);

		float diffPitchRatePitch = Math.abs(diffPitchRate - diffPitch);
		float diffYawRateYaw = Math.abs(diffYawRate - diffYaw);

		if (diffYaw > 0.05f && diffPitch > 0.05 && (diffPitchRate > 1.0 || diffYawRate > 1.0) &&
		    (diffPitchRatePitch > 1.0f || diffYawRateYaw > 1.0f) && diffYawPitch < 0.009f && diffYawPitch > 0.001f) {
			fail("pr=%.3f,yr=%.3f,lpr=%.3f,lyr=%.3f,dp=%.3f,dy=%.3f,prr=%.3f,pyy=%.3f",
					diffPitchRate, diffYawRate,
					this.lastPitchRate, this.lastYawRate,
					diffPitch, diffYaw,
					diffPitchRatePitch, diffYawRateYaw);
		}

		this.lastYawRate = diffYaw;
		this.lastPitchRate = diffPitch;
	}

}
