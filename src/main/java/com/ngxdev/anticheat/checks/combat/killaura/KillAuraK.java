package com.ngxdev.anticheat.checks.combat.killaura;

import com.ngxdev.anticheat.api.check.Check;
import api.CheckType;
import com.ngxdev.tinyprotocol.packet.in.WrappedInClientCommandPacket;
import com.ngxdev.tinyprotocol.packet.in.WrappedInFlyingPacket;
import com.ngxdev.tinyprotocol.packet.in.WrappedInUseEntityPacket;

import static api.CheckType.Type.KILLAURA;
import static com.ngxdev.tinyprotocol.packet.in.WrappedInClientCommandPacket.EnumClientCommand.OPEN_INVENTORY_ACHIEVEMENT;

@CheckType(id = "killaura:k", name = "KillAura K", type = KILLAURA, maxVl = 3)
public class KillAuraK extends Check {
    private boolean sent;

    void check(WrappedInFlyingPacket packet) {
        this.sent = false;
    }

    void check(WrappedInUseEntityPacket packet) {
        this.sent = true;
    }

    void check(WrappedInClientCommandPacket packet) {
        if (!canCheck()) return;
        if (packet.getCommand() == OPEN_INVENTORY_ACHIEVEMENT) {
            if (this.sent) {
                if (fail()) player.closeInventory();
            }
        }
    }
}
