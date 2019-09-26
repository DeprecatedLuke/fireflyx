package com.ngxdev.anticheat.checks.badpackets;

import com.ngxdev.anticheat.api.check.Check;
import api.CheckType;
import com.ngxdev.tinyprotocol.packet.in.WrappedInFlyingPacket;

import static api.CheckType.Type.BADPACKET;

@CheckType(id = "badpackets:b", name = "BadPackets B", type = BADPACKET)
public class BadPacketsB extends Check {
    void check(WrappedInFlyingPacket packet) {
        if (!canCheck()) return;
        if (Math.abs(packet.getPitch()) > 90.0f) {
            if (fail()) setback();
        }
    }
}
