package com.ngxdev.anticheat.checks.movement.flight;

import api.CheckType;
import com.ngxdev.anticheat.api.check.Check;
import com.ngxdev.tinyprotocol.packet.in.WrappedInFlyingPacket;
import org.bukkit.event.player.PlayerTeleportEvent;

import static api.CheckType.State.BETA;
import static api.CheckType.Type.MOVEMENT;

@CheckType(id = "glide", name = "Glide", type = MOVEMENT, state = BETA)
public class Glide extends Check {
	private double fall;
	private double fallSpeedLimit;
	private int fallSpeedVl;
	private int ticksWhileFalling;

	void check(PlayerTeleportEvent event) {
		reset();
	}

	void check(WrappedInFlyingPacket packet) {
		double dy = data.movement.deltaV;
		if (!packet.isPos()
				|| !canCheckMovement()
				|| isGliding()) return;

		if (isAccurateOnGround(packet) && data.movement.deltaV != 0) {
			reset();

			ticksWhileFalling = 0;
			return;
		}

		if (dy > -0.0981 && dy < -0.0979) return;

		if (dy > 0.0) {
			ticksWhileFalling = 0;
		}

		if (dy >= 0) return;

		if (data.velocity.deltaH != 0) {
			fall = dy;
			return;
		}

		double diff = Math.abs(fall) - Math.abs(dy);
		//debug("d=%.4f,l=%.4f,f=%.4f,fs=%.4f",  diff, fall, dy, Math.abs(fallDistance) - Math.abs(dy));

		boolean canFail = data.enviorment.onGround.hasPassed(2)
				&& data.enviorment.onLadder.hasPassed(20)
				&& data.enviorment.inLiquid.hasPassed(2)
				&& data.timers.join.hasPassed(20);

		boolean fail = false;
		if (diff > 0.4 && canFail) {
			fall = dy;
			if (!(data.timers.lastTeleport.hasNotPassed(2) || data.timers.lastFlightToggle.hasNotPassed(2))) {
				if (fail("t=slow,d=%.5f,l=%.5f", dy, diff)) {
					setback(diff);
					fail = true;
				}
			}
		}

		if (dy < 0.0) {
			++ticksWhileFalling;
			double diff2 = this.fallSpeedLimit - dy;
			if (diff2 < 0.4 && ++this.fallSpeedVl == 6) {
				this.fallSpeedVl = 0;
				this.fallSpeedLimit = dy + 0.2;
//				if (!(data.timers.lastTeleport.hasNotPassed(2) || data.timers.lastFlightToggle.hasNotPassed(2))) {
//					if (fail("t=fall,d=%.5f,l=%.5f", dy, this.fallSpeedLimit)) {
//						fallSpeedVl = 0;
//						setback(diff);
//						fail = true;
//					}
//				}
			}

			this.fallSpeedLimit -= 0.08;
		}

		if (ticksWhileFalling > 0 && ticksWhileFalling < 4) {
			if (diff < -0.4 && canFail) {
				fall = dy;
				if (!(data.timers.lastTeleport.hasNotPassed(2) || data.timers.lastFlightToggle.hasNotPassed(23))) {
					if (fail("t=fast,d=%.5f,l=%.5f", dy, diff)) {
						setback(diff);
						fail = true;
					}
				}
			}
		}

		//TODO: this wont work. sorry
		//TODO: check ticks in air and fall speed
		/* else if (diff < -0.4 && canFail) {
			fall = dy;
			if (data.timers.lastFlightToggle.hasPassed(10)) {
				if (fail("t=fast,d=%.5f,l=%.5f", dy, diff)) {
					setback(diff);
					fail = true;
				}
			}
		}*/
		if (!fail) {
			fall += 0.08;
			fall *= data.enviorment.inWeb.wasReset() ? 0.05000000074505806D : 0.9800000190734863D;
		}
	}

	public void reset() {
		fallSpeedLimit = 0.078;
		fallSpeedVl = 0;
		fall = 0.0784000015258789;
	}
}
