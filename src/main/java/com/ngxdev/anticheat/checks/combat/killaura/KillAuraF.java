package com.ngxdev.anticheat.checks.combat.killaura;

import com.ngxdev.anticheat.api.check.Check;
import api.CheckType;
import com.ngxdev.tinyprotocol.api.ProtocolVersion;
import com.ngxdev.tinyprotocol.packet.in.WrappedInBlockDigPacket;
import com.ngxdev.tinyprotocol.packet.in.WrappedInFlyingPacket;
import com.ngxdev.tinyprotocol.packet.in.WrappedInUseEntityPacket;

import static api.CheckType.Type.KILLAURA;

@CheckType(id = "killaura:f", name = "KillAura F", type = KILLAURA, maxVl = 3)
public class KillAuraF extends Check {
    private boolean sent;

    void check(WrappedInFlyingPacket packet) {
        this.sent = false;
    }

    void check(WrappedInBlockDigPacket packet) {
        WrappedInBlockDigPacket.EnumPlayerDigType digType = packet.getAction();
        if (digType == WrappedInBlockDigPacket.EnumPlayerDigType.START_DESTROY_BLOCK
                || digType == WrappedInBlockDigPacket.EnumPlayerDigType.ABORT_DESTROY_BLOCK
                || digType == WrappedInBlockDigPacket.EnumPlayerDigType.RELEASE_USE_ITEM) {
            this.sent = true;
        }
    }

    void check(WrappedInUseEntityPacket packet) {
        if (!canCheck() || data.protocolVersion.isAbove(ProtocolVersion.V1_8_9)) return;
        if (this.sent) {
            if (fail()) packet.setCancelled(true);
        }
    }
}
