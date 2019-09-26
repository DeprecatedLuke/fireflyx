package com.ngxdev.anticheat.checks.inventory;

import com.ngxdev.anticheat.api.check.Check;
import api.CheckType;
import com.ngxdev.tinyprotocol.packet.in.WrappedInClientCommandPacket;
import com.ngxdev.tinyprotocol.packet.in.WrappedInCloseWindowPacket;
import com.ngxdev.tinyprotocol.packet.in.WrappedInFlyingPacket;

import static api.CheckType.Type.INVENTORY;
import static com.ngxdev.tinyprotocol.packet.in.WrappedInClientCommandPacket.EnumClientCommand.OPEN_INVENTORY_ACHIEVEMENT;

@CheckType(id = "inventory:f", name = "Inventory F", type = INVENTORY)
public class InventoryF extends Check {
    private boolean sent;

    void check(WrappedInFlyingPacket packet) {
        this.sent = false;
    }

    void check(WrappedInCloseWindowPacket packet) {
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
