package com.ngxdev.anticheat.checks.combat.killaura;

import com.ngxdev.anticheat.api.check.Check;
import api.CheckType;
import com.ngxdev.tinyprotocol.packet.in.WrappedInEntityActionPacket;
import com.ngxdev.tinyprotocol.packet.in.WrappedInFlyingPacket;
import com.ngxdev.tinyprotocol.packet.in.WrappedInUseEntityPacket;

import static api.CheckType.Type.KILLAURA;
import static com.ngxdev.tinyprotocol.packet.in.WrappedInUseEntityPacket.EnumEntityUseAction.ATTACK;

@CheckType(id = "killaura:i", name = "KillAura I", type = KILLAURA, maxVl = 3)
public class KillAuraI extends Check {
    private boolean sent;

    void check(WrappedInFlyingPacket packet) {
        this.sent = false;
    }

    void check(WrappedInEntityActionPacket packet) {
        WrappedInEntityActionPacket.EnumPlayerAction action = packet.getAction();
        if (action == WrappedInEntityActionPacket.EnumPlayerAction.START_SPRINTING
                || action == WrappedInEntityActionPacket.EnumPlayerAction.STOP_SPRINTING
                || action == WrappedInEntityActionPacket.EnumPlayerAction.START_SNEAKING
                || action == WrappedInEntityActionPacket.EnumPlayerAction.STOP_SNEAKING) {
            this.sent = true;
        }
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
