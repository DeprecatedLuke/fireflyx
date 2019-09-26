package com.ngxdev.anticheat.checks.badpackets.experimental;

import com.ngxdev.anticheat.api.check.Check;
import api.CheckType;
import com.ngxdev.tinyprotocol.packet.in.WrappedInBlockPlacePacket;
import com.ngxdev.tinyprotocol.packet.in.WrappedInFlyingPacket;
import com.ngxdev.tinyprotocol.packet.in.WrappedInHeldItemSlotPacket;

import static api.CheckType.Type.BADPACKET;

@CheckType(id = "badpackets:3", name = "BadPackets 3", type = BADPACKET, maxVl = 3, state = CheckType.State.EXPERIMENTAL)
public class BadPackets3 extends Check {
    private boolean sent;

    void check(WrappedInFlyingPacket packet) {
        this.sent = false;
    }

    void check(WrappedInBlockPlacePacket packet) {
        this.sent = true;
    }

    void check(WrappedInHeldItemSlotPacket packet) {
        if (!canCheck()) return;
        if (this.sent) if (fail()) packet.setCancelled(true);
    }
}
