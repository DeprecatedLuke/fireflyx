package com.ngxdev.anticheat.checks.combat.killaura;

import com.ngxdev.anticheat.api.check.Check;
import api.CheckType;
import com.ngxdev.tinyprotocol.api.ProtocolVersion;
import com.ngxdev.tinyprotocol.packet.in.WrappedInBlockDigPacket;
import com.ngxdev.tinyprotocol.packet.in.WrappedInFlyingPacket;
import com.ngxdev.tinyprotocol.packet.in.WrappedInUseEntityPacket;

import static api.CheckType.Type.KILLAURA;
import static api.ConfigValueX.BLOCK_HITS_TIME;
import static com.ngxdev.tinyprotocol.packet.in.WrappedInBlockDigPacket.EnumPlayerDigType.RELEASE_USE_ITEM;
import static com.ngxdev.tinyprotocol.packet.in.WrappedInBlockDigPacket.EnumPlayerDigType.START_DESTROY_BLOCK;

@CheckType(id = "killaura:h", name = "KillAura H", type = KILLAURA, maxVl = 3)
public class KillAuraH extends Check {
    private boolean sent;

    void check(WrappedInBlockDigPacket packet) {
        if (!canCheck() || data.protocolVersion.isAbove(ProtocolVersion.V1_8_9)) return;
        WrappedInBlockDigPacket.EnumPlayerDigType digType = packet.getAction();
        if ((digType == START_DESTROY_BLOCK || digType == RELEASE_USE_ITEM) && this.sent) {
            if (fail()) data.state.cancelHits = BLOCK_HITS_TIME.asInteger();
        }
    }

    void check(WrappedInUseEntityPacket packet) {
        this.sent = true;
    }

    void check(WrappedInFlyingPacket packet) {
        this.sent = false;
    }
}
