package com.ngxdev.anticheat.checks.badpackets;

import com.ngxdev.anticheat.api.check.Check;
import api.CheckType;
import com.ngxdev.tinyprotocol.packet.in.WrappedInEntityActionPacket;

import static api.CheckType.Type.BADPACKET;
import static com.ngxdev.tinyprotocol.packet.in.WrappedInEntityActionPacket.EnumPlayerAction.START_SPRINTING;
import static com.ngxdev.tinyprotocol.packet.in.WrappedInEntityActionPacket.EnumPlayerAction.STOP_SPRINTING;


@CheckType(id = "badpackets:g", name = "BadPackets G", type = BADPACKET, maxVl = 3)
public class BadPacketsG extends Check {
    private WrappedInEntityActionPacket.EnumPlayerAction lastAction;

    void check(WrappedInEntityActionPacket packet) {
        if (!canCheck()) return;
        if (packet.getAction() == START_SPRINTING || packet.getAction() == STOP_SPRINTING) {
            if (this.lastAction == packet.getAction() && data.timers.lastAttack.hasNotPassed(20 * 10)) {
                if (fail()) setback();
            }
            this.lastAction = packet.getAction();
        }
    }
}
