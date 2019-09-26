package com.ngxdev.anticheat.checks.combat.killaura.experimental;

import com.ngxdev.anticheat.api.check.Check;
import api.CheckType;
import com.ngxdev.tinyprotocol.packet.in.WrappedInArmAnimationPacket;
import com.ngxdev.tinyprotocol.packet.in.WrappedInBlockDigPacket;
import com.ngxdev.tinyprotocol.packet.in.WrappedInFlyingPacket;

import static api.CheckType.State.EXPERIMENTAL;
import static api.CheckType.Type.KILLAURA;
import static com.ngxdev.tinyprotocol.packet.in.WrappedInBlockDigPacket.EnumPlayerDigType.STOP_DESTROY_BLOCK;

@CheckType(id = "killaura:0", name = "KillAura 0", type = KILLAURA, state = EXPERIMENTAL)
public class KillAura0 extends Check {
    private boolean sent;

    void check(WrappedInFlyingPacket packet) {
        this.sent = false;
    }


    void check(WrappedInArmAnimationPacket packet) {
        this.sent = true;
    }

    void check(WrappedInBlockDigPacket packet) {
        if (!canCheck()) return;
        if (packet.getAction() == STOP_DESTROY_BLOCK) {
            if (this.sent) {
                if (fail()) packet.setCancelled(true);
            }
        }
    }
}
