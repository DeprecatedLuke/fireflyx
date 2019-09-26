package com.ngxdev.anticheat.checks.combat.killaura;

import com.ngxdev.anticheat.api.check.Check;
import api.CheckType;
import com.ngxdev.tinyprotocol.packet.in.WrappedInCloseWindowPacket;
import com.ngxdev.tinyprotocol.packet.in.WrappedInFlyingPacket;
import com.ngxdev.tinyprotocol.packet.in.WrappedInUseEntityPacket;

import static api.CheckType.Type.KILLAURA;
import static com.ngxdev.tinyprotocol.packet.in.WrappedInUseEntityPacket.EnumEntityUseAction.ATTACK;

@CheckType(id = "killaura:j", name = "KillAura J", type = KILLAURA, maxVl = 3)
public class KillAuraJ extends Check {
    private boolean sent;

    void check(WrappedInFlyingPacket packet) {
        this.sent = false;
    }

    void check(WrappedInCloseWindowPacket packet) {
        this.sent = true;
    }

    void check(WrappedInUseEntityPacket packet) {
        if (!canCheck()) return;
        if (packet.getAction() == ATTACK) {
            if (this.sent) {
                if (fail()) packet.setCancelled(true);
            }
        }
    }
}
