package com.ngxdev.anticheat.checks.inventory;

import com.ngxdev.anticheat.api.check.Check;
import api.CheckType;
import com.ngxdev.tinyprotocol.packet.in.WrappedInClientCommandPacket;
import com.ngxdev.tinyprotocol.packet.in.WrappedInFlyingPacket;
import com.ngxdev.tinyprotocol.packet.in.WrappedInWindowClickPacket;

import static api.CheckType.Type.INVENTORY;
import static com.ngxdev.tinyprotocol.packet.in.WrappedInClientCommandPacket.EnumClientCommand.OPEN_INVENTORY_ACHIEVEMENT;

@CheckType(id = "inventory:e", name = "Inventory E", type = INVENTORY)
public class InventoryE extends Check {
    private boolean sent;

    void check(WrappedInFlyingPacket packet) {
        this.sent = false;
    }

    void check(WrappedInWindowClickPacket packet) {
        if (!canCheck()) return;
        if (this.sent) {
            fail();
        }
    }

    void check(WrappedInClientCommandPacket packet) {
        if (packet.getCommand() == OPEN_INVENTORY_ACHIEVEMENT) {
            this.sent = true;
        }
    }
}
