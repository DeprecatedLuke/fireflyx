package com.ngxdev.anticheat.checks.misc;

import api.CheckType;
import com.ngxdev.anticheat.api.check.Check;
import com.ngxdev.tinyprotocol.api.ProtocolVersion;
import com.ngxdev.tinyprotocol.packet.in.WrappedInTransactionPacket;

import static api.CheckType.State.BETA;
import static api.CheckType.Type.EXPLOIT;

//@CheckType(id = "blink", name = "Blink", type = EXPLOIT, state = BETA, alert = false, cancel = true)
public class Blink extends Check {
	void parse(WrappedInTransactionPacket packet) {
		if (player.isDead()) return;
		if (data.timers.lastPacket.hasPassed((data.protocolVersion.isAbove(ProtocolVersion.V1_8_9) ? 1500 : 500) + data.lag.transactionPing)) {
			if (fail(3, 20 * (data.protocolVersion.isAbove(ProtocolVersion.V1_8_9) ? 2 : 1) * 3, "%i", data.timers.lastPacket.getPassed())) {
				setback();
			}
		}
	}
}
