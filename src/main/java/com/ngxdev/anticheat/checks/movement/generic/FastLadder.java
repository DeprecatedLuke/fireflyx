package com.ngxdev.anticheat.checks.movement.generic;

import com.ngxdev.anticheat.api.check.Check;
import api.CheckType;
import com.ngxdev.tinyprotocol.packet.in.WrappedInFlyingPacket;

import static api.CheckType.State.BETA;
import static api.CheckType.Type.MOVEMENT;

@CheckType(id = "generic:fastladder", name = "Fast Ladder", type = MOVEMENT, state = BETA, maxVl = 2, timeout = 10)
public class FastLadder extends Check {
    void check(WrappedInFlyingPacket packet) {
        double dy = data.movement.deltaV;
        if (!packet.isPos()
                || !isMoving()
                || !canCheckMovement()
                || data.enviorment.onLadder.wasNotReset()
                || isGliding()) return;

        if (Math.abs(data.movement.lastDeltaV) <= Math.abs(data.movement.deltaV)
                && data.movement.lastDeltaV != 0
                && data.movement.deltaV != 0) {
            if (dy > 0.118) {
                if (fail("d=%.4f", dy)) setback();
            } else if (dy < -0.151) {
                if (fail("d=%.4f", dy)) setback();
            }
        }
    }
}
