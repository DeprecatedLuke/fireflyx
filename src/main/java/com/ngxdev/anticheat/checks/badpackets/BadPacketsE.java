package com.ngxdev.anticheat.checks.badpackets;

import com.ngxdev.anticheat.api.check.Check;
import api.CheckType;
import com.ngxdev.tinyprotocol.packet.in.WrappedInBlockDigPacket;

import static api.CheckType.Type.BADPACKET;
import static com.ngxdev.tinyprotocol.packet.in.WrappedInBlockDigPacket.EnumPlayerDigType.RELEASE_USE_ITEM;


@CheckType(id = "badpackets:e", name = "BadPackets E", type = BADPACKET, maxVl = 3)
public class BadPacketsE extends Check {
    void check(WrappedInBlockDigPacket packet) {
        if (!canCheck()) return;
        if (packet.getAction() == RELEASE_USE_ITEM && data.state.isPlacing) {
            if (fail()) setback();
        }
    }
}
