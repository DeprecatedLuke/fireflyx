package com.ngxdev.anticheat.checks.combat.aimassist;

import com.ngxdev.anticheat.api.check.Check;
import api.CheckType;
import com.ngxdev.tinyprotocol.packet.in.WrappedInFlyingPacket;
import org.bukkit.util.Vector;

import static api.CheckType.Type.COMBAT;

@CheckType(id = "aimassist:c", name = "AimAssist C", type = COMBAT)
public class AimAssistC extends Check {
	private double multiplier = Math.pow(2.0, 24.0);
	private float lastPitch = -1;
	private long[] gcdLog = new long[10];
	private int current = 0;

	void check(WrappedInFlyingPacket packet) {
		if (!canCheck()
				|| !packet.isLook()) return;

		Vector first = new Vector(data.movement.deltaYaw, 0, data.movement.deltaPitch);
		Vector second = new Vector(data.movement.lastDeltaYaw, 0, data.movement.lastDeltaPitch);

		double angle = Math.pow(first.angle(second) * 180, 2);

		boolean flagged = false;

		long deviation = getDeviation(data.movement.deltaPitch);

		gcdLog[current % gcdLog.length] = deviation;
		current++;

		if (data.movement.tpitch > -20 && data.movement.tpitch < 20
				&& data.movement.deltaPitch > 0
				&& data.movement.deltaYaw > 1
				&& data.movement.deltaYaw < 10
				&& data.movement.lastDeltaYaw <= data.movement.deltaYaw
				&& data.movement.yawDifference != 0
				&& data.movement.yawDifference < 1
				&& angle > 2500
		) {

			if (current > gcdLog.length) {
				long maxDeviation = 0;
				for (long l : gcdLog) if (deviation != 0 && l != 0) maxDeviation = Math.max(Math.max(l, deviation) % Math.min(l, deviation), maxDeviation);
			}
			if (deviation > 0) {
				flagged = true;
				if (fail(2, 20 * 5)) setback(true);
				reset();
			}
		}
		debug("y1=%.5f,y2=%.5f,a=%.5f" + (flagged ? " §c§lFLAGGED" : ""), data.movement.deltaYaw, data.movement.yawDifference, angle);
	}

	public long getDeviation(float pitchChange) {
		if (lastPitch != -1) {
			try {
				long current = (long) (pitchChange * multiplier);
				long last = (long) (lastPitch * multiplier);
				long value = convert(current, last);

				if (value < 0x20000) {
					return value;
				}
			} catch (Exception e) {
			}
		}

		lastPitch = pitchChange;
		return -1;
	}

	public void reset() {
		lastPitch = -1;
	}

	private long convert(long current, long last) {
		if (last <= 16384) return current;
		return convert(last, current % last);
	}
}
