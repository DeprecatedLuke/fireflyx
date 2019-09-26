package com.ngxdev.anticheat.checks.movement.generic;

import api.CheckType;
import com.ngxdev.anticheat.api.check.Check;
import com.ngxdev.anticheat.api.check.Priority;
import com.ngxdev.tinyprotocol.packet.in.WrappedInFlyingPacket;
import org.bukkit.potion.PotionEffectType;

import static api.CheckType.Type.MOVEMENT;

@CheckType(id = "vclip", name = "VClip", type = MOVEMENT)
public class VClip extends Check {
	@Priority(-3)
	void check(WrappedInFlyingPacket packet) {
		if (!packet.isPos()
				|| !(!data.state.isTeleporing && !data.state.isSettingback && player.getVehicle() == null && data.timers.join.hasPassed(10) && data.enviorment.handler != null)
				|| data.enviorment.onLadder.hasNotPassed(5)
				|| data.timers.slimePush.hasNotPassed(10)
				|| data.enviorment.onSlime.hasNotPassed(10)
				|| isGliding()) return;

		double effect = (.2 * getPotionEffectLevel(PotionEffectType.JUMP));

		double deltaDeltaChange = Math.abs(data.movement.deltaV) - Math.abs(data.movement.lastDeltaV);
		if (deltaDeltaChange < 0) return;
		if (data.movement.fy > data.movement.ty) deltaDeltaChange = -deltaDeltaChange;

		if (deltaDeltaChange < (-0.8)) {
			if (fail("down,d=%.2f", deltaDeltaChange)) {
				setback();
			}
		}


		if (deltaDeltaChange > (0.8 + (data.movement.hasJumped ? 0.47 : 0) + effect)) {
			//System.out.println("FAIL");
			if (fail("up,d=%.2f", deltaDeltaChange)) {
				setback();
			}
		}
	}
}
