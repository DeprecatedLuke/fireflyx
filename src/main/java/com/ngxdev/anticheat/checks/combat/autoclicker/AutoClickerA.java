package com.ngxdev.anticheat.checks.combat.autoclicker;

import com.ngxdev.anticheat.api.check.Check;
import api.CheckType;
import com.ngxdev.tinyprotocol.api.ProtocolVersion;
import com.ngxdev.tinyprotocol.packet.in.WrappedInArmAnimationPacket;
import com.ngxdev.tinyprotocol.packet.in.WrappedInFlyingPacket;

import static api.CheckType.Type.AUTOCLICKER;

@CheckType(id = "autoclicker:a", name = "AutoClicker A", type = AUTOCLICKER, maxVl = 4)
public class AutoClickerA extends Check {
	private int swings;
	private int movements;

	void check(WrappedInFlyingPacket packet) {
		if (!canCheck() || data.protocolVersion.isAbove(ProtocolVersion.V1_8_9)) return;
		if (++this.movements == 20) {
			if (this.swings > 20) {
				fail("c=%d", swings);
			}

			data.autoclicker.cps = swings;
			this.movements = this.swings = 0;
		}
	}

	void check(WrappedInArmAnimationPacket packet) {
		if (!data.state.isDigging && !data.state.isPlacing) {
			++this.swings;
		}
	}
}