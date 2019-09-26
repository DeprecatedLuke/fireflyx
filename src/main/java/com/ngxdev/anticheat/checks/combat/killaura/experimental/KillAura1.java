package com.ngxdev.anticheat.checks.combat.killaura.experimental;

import com.ngxdev.anticheat.api.check.Check;
import api.CheckType;
import com.ngxdev.tinyprotocol.packet.in.WrappedInFlyingPacket;
import com.ngxdev.tinyprotocol.packet.in.WrappedInHeldItemSlotPacket;
import com.ngxdev.tinyprotocol.packet.in.WrappedInUseEntityPacket;

import static api.CheckType.State.EXPERIMENTAL;
import static api.CheckType.Type.KILLAURA;
import static com.ngxdev.tinyprotocol.packet.in.WrappedInUseEntityPacket.EnumEntityUseAction.ATTACK;

@CheckType(id = "killaura:1", name = "KillAura 1", type = KILLAURA, state = EXPERIMENTAL)
public class KillAura1 extends Check {
    private boolean sent;

    void check(WrappedInFlyingPacket packet) {
        this.sent = false;
    }

    void check(WrappedInUseEntityPacket packet) {
        if (packet.getAction() == ATTACK) {
            this.sent = true;
        }
    }

    void check(WrappedInHeldItemSlotPacket packet) {
        if (!canCheck()) return;
        if (this.sent) {
            if (fail()) packet.setCancelled(true);
        }
    }
}
