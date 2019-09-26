package com.ngxdev.anticheat.checks.combat.killaura;

import com.ngxdev.anticheat.api.check.Check;
import api.CheckType;
import com.ngxdev.tinyprotocol.packet.in.WrappedInUseEntityPacket;

import static api.CheckType.Type.KILLAURA;
import static com.ngxdev.tinyprotocol.packet.in.WrappedInUseEntityPacket.EnumEntityUseAction.ATTACK;

@CheckType(id = "killaura:d", name = "KillAura D", type = KILLAURA, maxVl = 3)
public class KillAuraD extends Check {
    void check(WrappedInUseEntityPacket packet) {
        if (!canCheck()) return;
        if (packet.getAction() == ATTACK && data.state.isPlacing) {
            if (fail()) packet.setCancelled(true);
        }
    }
}
