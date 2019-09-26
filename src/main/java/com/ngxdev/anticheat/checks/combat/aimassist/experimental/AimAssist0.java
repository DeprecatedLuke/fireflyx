package com.ngxdev.anticheat.checks.combat.aimassist.experimental;

import com.ngxdev.anticheat.api.check.Check;
import api.CheckType;
import com.ngxdev.tinyprotocol.packet.in.WrappedInFlyingPacket;

import static api.CheckType.State.EXPERIMENTAL;
import static api.CheckType.Type.COMBAT;

@CheckType(id = "aimassist:0", name = "AimAssist 0", type = COMBAT, state = EXPERIMENTAL)
public class AimAssist0 extends Check {
    private double vl;

    void check(WrappedInFlyingPacket packet) {
        if (!canCheck()
                || data.timers.lastAttack.hasPassed(20 * 10)
                || !packet.isLook()) return;

        final float diffYaw = data.movement.deltaYaw;
        if (data.movement.fpitch == data.movement.tpitch && diffYaw >= 3.0f && data.movement.fpitch != 90.0) {
            if ((vl += 0.9) >= 6.3) {
                fail("y=%.1f,d=%.1f", diffYaw, vl);
            }
        } else {
            vl -= 1.6;
        }
    }
}
