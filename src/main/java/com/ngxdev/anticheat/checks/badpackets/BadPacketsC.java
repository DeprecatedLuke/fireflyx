package com.ngxdev.anticheat.checks.badpackets;

import com.ngxdev.anticheat.api.check.Check;
import api.CheckType;
import com.ngxdev.tinyprotocol.packet.in.WrappedInEntityActionPacket;
import com.ngxdev.tinyprotocol.packet.in.WrappedInFlyingPacket;

import static api.CheckType.Type.BADPACKET;
import static com.ngxdev.tinyprotocol.packet.in.WrappedInEntityActionPacket.EnumPlayerAction.START_SPRINTING;
import static com.ngxdev.tinyprotocol.packet.in.WrappedInEntityActionPacket.EnumPlayerAction.STOP_SPRINTING;

@CheckType(id = "badpackets:c", name = "BadPackets C", type = BADPACKET, maxVl = 3)
public class BadPacketsC extends Check {
    private boolean sent;

    void check(WrappedInFlyingPacket packet) {
        this.sent = false;
    }

    void check(WrappedInEntityActionPacket packet) {
        if (!canCheck()) return;
        if (packet.getAction() == START_SPRINTING || packet.getAction() == STOP_SPRINTING) {
            if (this.sent) {
                if (fail()) setback();
            } else {
                this.sent = true;
            }
        }
    }
}
