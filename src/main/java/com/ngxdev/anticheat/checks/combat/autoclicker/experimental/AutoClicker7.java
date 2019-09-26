package com.ngxdev.anticheat.checks.combat.autoclicker.experimental;

import com.ngxdev.anticheat.api.check.Check;
import api.CheckType;
import com.ngxdev.tinyprotocol.api.ProtocolVersion;
import com.ngxdev.tinyprotocol.packet.in.WrappedInArmAnimationPacket;
import com.ngxdev.tinyprotocol.packet.in.WrappedInFlyingPacket;

import static api.CheckType.Type.AUTOCLICKER;

@CheckType(id = "autoclicker:7", name = "AutoClicker 7", type = AUTOCLICKER, state = CheckType.State.EXPERIMENTAL)
public class AutoClicker7 extends Check {
    int zeroDelayClicks;

    void check(WrappedInFlyingPacket packet) {
        zeroDelayClicks = 0;
    }

    void check(WrappedInArmAnimationPacket packet) {
        if (!canCheck() || data.state.isPlacing || data.state.isDigging || data.protocolVersion.isAbove(ProtocolVersion.V1_8_9)) return;

        // Zero Delay
        zeroDelayClicks++;

        if (zeroDelayClicks > 2) {
            if (data.lag.packetDelay < 50) fail();
        }
    }
}
