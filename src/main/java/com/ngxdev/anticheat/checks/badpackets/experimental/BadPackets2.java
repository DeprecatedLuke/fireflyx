package com.ngxdev.anticheat.checks.badpackets.experimental;

import com.ngxdev.anticheat.api.check.Check;
import api.CheckType;
import com.ngxdev.tinyprotocol.packet.in.WrappedInEntityActionPacket;
import com.ngxdev.tinyprotocol.packet.in.WrappedInFlyingPacket;

import static api.CheckType.Type.BADPACKET;
import static com.ngxdev.tinyprotocol.packet.in.WrappedInEntityActionPacket.EnumPlayerAction.START_SNEAKING;
import static com.ngxdev.tinyprotocol.packet.in.WrappedInEntityActionPacket.EnumPlayerAction.STOP_SNEAKING;

@CheckType(id = "badpackets:2", name = "BadPackets 2", type = BADPACKET, maxVl = 3, state = CheckType.State.EXPERIMENTAL)
public class BadPackets2 extends Check {
    private boolean sent;

    void check(WrappedInFlyingPacket packet) {
        this.sent = false;
    }

    void check(WrappedInEntityActionPacket packet) {
        if (!canCheck()) return;
        if (packet.getAction() == START_SNEAKING || packet.getAction() == STOP_SNEAKING) {
            if (this.sent) {
                if (fail()) setback();
            } else {
                this.sent = true;
            }
        }
    }
}
