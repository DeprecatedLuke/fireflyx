package com.ngxdev.anticheat.checks.movement.flight;

import api.CheckType;
import com.ngxdev.anticheat.api.check.Check;
import com.ngxdev.tinyprotocol.packet.in.WrappedInFlyingPacket;
import com.ngxdev.tinyprotocol.packet.out.WrappedOutVelocityPacket;
import org.bukkit.potion.PotionEffectType;

import static api.CheckType.State.BETA;
import static api.CheckType.Type.MOVEMENT;

@CheckType(id = "flight", name = "Flight", type = MOVEMENT, state = BETA)
public class Flight extends Check {
	private double ascension;
	private double jump;
	private double velOffset;

	private double jumpLimit;
	private double ascensionLimit;
	private boolean slimeJump = false;

	void check(WrappedOutVelocityPacket packet) {

	}

	void check(WrappedInFlyingPacket packet) {
		double dy = data.movement.deltaV;

		if (!packet.isPos()
				|| !canCheckMovement()
				|| isGliding()) return;

		boolean slime = data.enviorment.onSlime.hasNotPassed(3);
		if (data.enviorment.onGround.wasReset()) {
			slimeJump = false;
			reset();
			if (data.timers.slimePush.hasNotPassed(5)) {
				if (!slimeJump) {
					jump += 0.5;
					jumpLimit += 0.75;
					slimeJump = true;
				}
			} else if (data.enviorment.onSlime.wasReset()) {
				jump += Math.abs(data.movement.lastDeltaV) / 2;
				jumpLimit += Math.abs(data.movement.lastDeltaV);
				slimeJump = true;
			}
			return;
		}

		if (dy <= 0) return;

		if (data.velocity.deltaV != 0) {
			velOffset += data.velocity.deltaV * (data.timers.lastWankVelocity.hasNotPassed(10) ? 2.2 : 1.5);
		}

		ascension += dy;
		ascensionLimit += jumpLimit;

		double jumpOffset = 0.2 * getPotionEffectLevel(PotionEffectType.JUMP);

		boolean canFail = data.enviorment.onGround.hasPassed(4)
				&& data.enviorment.onLadder.hasPassed(5)
				&& data.enviorment.inLiquid.hasPassed(5)
				&& data.timers.join.hasPassed(20)
				&& data.timers.lastBlockGlitch.hasPassed(5);

		if (dy > (jumpLimit + velOffset + jumpOffset + 0.2) && canFail) {
			if (data.timers.lastFlightToggle.hasPassed(10))
				if (fail("t=jump,d=%.5f,l=%.5f,v=%.5f", dy, jumpLimit + velOffset, velOffset))
					setback(dy);
			jumpLimit = dy;
			ascensionLimit = ascension - 0.1;
		} else {
			if (ascension > ascensionLimit + velOffset + 0.2 && canFail) {
				if (data.timers.lastFlightToggle.hasPassed(10))
					if (fail("t=asc,d=%.5f,l=%.5f,v=%.5f", ascension, ascensionLimit + velOffset, velOffset))
						setback(dy);
				jumpLimit = dy;
				ascensionLimit = ascension - 0.1;
			}
		}
		if (jumpLimit - dy > (velOffset + jumpOffset + 0.2) && canFail) {
			if (data.enviorment.inLiquid.hasPassed(10) && data.enviorment.blockAbove.hasPassed(5)) {
				if (slimeJump) {
					//TODO
				} else {
					if (data.timers.lastFlightToggle.hasPassed(10))
						if (fail(2, 10, "t=jumpglide,d=%.5f,l=%.5f,v=%.5f", dy, jumpLimit, velOffset))
							setback(dy);
				}
			}
		}


		jumpLimit *= jump;
		jump -= 0.025;
	}

	private void reset() {
		int jumpAmplifier = getPotionEffectLevel(PotionEffectType.JUMP);
		jump = 0.8 + 0.01 * jumpAmplifier;
		jumpLimit = 0.42 + 0.2 * jumpAmplifier;
		ascension = 0;
		ascensionLimit = 0;
		velOffset = 0;
	}
}
