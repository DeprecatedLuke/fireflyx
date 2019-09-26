package com.ngxdev.anticheat.checks.badpackets;

import com.ngxdev.anticheat.api.check.Check;
import api.CheckType;
import com.ngxdev.tinyprotocol.packet.in.WrappedInHeldItemSlotPacket;

import static api.CheckType.Type.BADPACKET;

@CheckType(id = "badpackets:h", name = "BadPackets H", type = BADPACKET, maxVl = 3)
public class BadPacketsH extends Check {
    private int lastSlot;

    void check(WrappedInHeldItemSlotPacket packet) {
        if (!canCheck()) return;
        int slot = packet.getSlot();
        if (this.lastSlot == slot) if (fail()) packet.setCancelled(true);
        this.lastSlot = slot;
    }
}
