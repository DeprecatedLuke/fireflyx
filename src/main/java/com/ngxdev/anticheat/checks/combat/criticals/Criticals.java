package com.ngxdev.anticheat.checks.combat.criticals;

import com.ngxdev.anticheat.api.check.Check;
import api.CheckType;
import com.ngxdev.tinyprotocol.packet.in.WrappedInUseEntityPacket;

import static api.CheckType.Type.MOVEMENT;

@CheckType(id = "criticals", name = "Criticals", type = MOVEMENT)
public class Criticals extends Check {
    void check(WrappedInUseEntityPacket packet) {
        if (data.enviorment.blockAbove.hasNotPassed(10)
                || data.enviorment.inWeb.hasNotPassed(10)) return;

        debug("f=%.2f,g=%s,d=%.2f,j=%s", player.getFallDistance(), data.enviorment.onGround.getPassed(), data.movement.deltaV, data.timers.lastJump.getPassed());
        if (player.getFallDistance() == 0 && !data.enviorment.onGround.hasPassed(4) && data.movement.deltaV < 0 && data.movement.deltaV > -0.0725) {
            if (fail(4, 20 * 10, "f=%.2f,g=%s,d=%.2f,j=%s", player.getFallDistance(), data.enviorment.onGround.getPassed(), data.movement.deltaV, data.timers.lastJump.getPassed())) packet.setCancelled(true);
        }
    }
}
