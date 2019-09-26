package com.ngxdev.anticheat.checks.combat.killaura.experimental;

import com.ngxdev.anticheat.api.check.Check;
import api.CheckType;
import com.ngxdev.tinyprotocol.api.ProtocolVersion;
import com.ngxdev.tinyprotocol.packet.in.WrappedInArmAnimationPacket;
import com.ngxdev.tinyprotocol.packet.in.WrappedInFlyingPacket;
import com.ngxdev.tinyprotocol.packet.in.WrappedInUseEntityPacket;

import static api.CheckType.Type.KILLAURA;
import static com.ngxdev.tinyprotocol.packet.in.WrappedInUseEntityPacket.EnumEntityUseAction.ATTACK;

@CheckType(id = "killaura:5", name = "KillAura 5", type = KILLAURA, maxVl = 6, state = CheckType.State.EXPERIMENTAL)
public class KillAura5 extends Check {
    private boolean sent;

    void check(WrappedInUseEntityPacket packet) {
        if (!canCheck() || data.protocolVersion.isAbove(ProtocolVersion.V1_8_9)) return;
        if (packet.getAction() == ATTACK) {
            if (!this.sent) {
                if (fail()) packet.setCancelled(true);
            } else {
                this.sent = false;
            }
        }
    }

    void check(WrappedInArmAnimationPacket packet) {
        this.sent = true;
    }

    void check(WrappedInFlyingPacket packet) {
        this.sent = false;
    }
}
