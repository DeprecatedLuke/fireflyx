package com.ngxdev.anticheat.checks.movement.experimental;

import com.ngxdev.anticheat.api.check.Check;
import api.CheckType;
import com.ngxdev.tinyprotocol.packet.in.WrappedInFlyingPacket;

import static api.CheckType.Type.MOVEMENT;

@CheckType(id = "velocity", name = "Velocity", type = MOVEMENT, state = CheckType.State.EXPERIMENTAL)
public class Velocity extends Check {
	public double vLoss;
	public double hLoss;

	void check(WrappedInFlyingPacket packet) {
		if (!packet.isPos()
				|| !canCheck()) return;
		double velocityModifier = isAccurateOnGround(packet) ? getBlockFriction() : 0.91;


		//debug("H: %.3f", Math.abs((data.movement.deltaH - data.movement.lastDeltaH)));
		if (data.velocity.deltaV != 0) {
			double hDiff = (data.movement.deltaH - data.velocity.deltaH);
			double vDiff = (data.movement.deltaV - data.velocity.deltaV);

			if (vDiff < 0) vLoss += vDiff;
			else if (hDiff < -0.5) hLoss += Math.abs(hDiff - 0.5);
			vDiff *= 0.98;
			hDiff *= 0.98;
			if (data.enviorment.collidedHorizontally.hasPassed(5)) {
				if (hLoss > 10) {
					hLoss = 0;
					//fail();
				}
			} else hLoss *= 0.9;

			debug("H: %.3f, V: %.3f, HL: %.3f", hDiff, vDiff, hLoss);
		}

		data.velocity.deltaH *= velocityModifier;
		data.velocity.deltaV -= 0.08;
		data.velocity.deltaV *= data.enviorment.inWeb.wasReset() ? 0.05000000074505806D : 0.9800000190734863D;
		if (data.velocity.deltaH < 0.001) {
			data.velocity.deltaH = 0;
		}
		if (data.velocity.deltaV < 0.001) {
			data.velocity.deltaV = 0;
		}
	}
}
