package com.ngxdev.anticheat.checks.combat.aimassist.experimental;

import com.ngxdev.anticheat.api.check.Check;
import api.CheckType;
import com.ngxdev.tinyprotocol.packet.in.WrappedInFlyingPacket;

import static api.CheckType.Type.COMBAT;

@CheckType(id = "aimassist:7", name = "AimAssist 7", type = COMBAT, maxVl = 20, timeout = 20 * 60, state = CheckType.State.EXPERIMENTAL)
public class AimAssist7 extends Check {
    private float suspiciousYaw;

    void check(WrappedInFlyingPacket packet) {
        if (!canCheck()
                || !packet.isLook()) return;

        float diffYaw = data.movement.deltaYaw;
        if (diffYaw > 1.0f && Math.round(diffYaw) == diffYaw && diffYaw % 1.5f != 0.0f) {
            if (diffYaw == this.suspiciousYaw) {
                fail("y=%.5f", diffYaw);
            }
            this.suspiciousYaw = Math.round(diffYaw);
        } else {
            this.suspiciousYaw = 0.0f;
        }
    }
}
