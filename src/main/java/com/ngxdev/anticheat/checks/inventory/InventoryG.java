package com.ngxdev.anticheat.checks.inventory;

import com.ngxdev.anticheat.api.check.Check;
import api.CheckType;
import com.ngxdev.tinyprotocol.packet.in.WrappedInClientCommandPacket;
import com.ngxdev.tinyprotocol.packet.in.WrappedInEntityActionPacket;
import com.ngxdev.tinyprotocol.packet.in.WrappedInFlyingPacket;
import com.ngxdev.tinyprotocol.packet.in.WrappedInSteerVehiclePacket;

import static api.CheckType.Type.INVENTORY;
import static com.ngxdev.tinyprotocol.packet.in.WrappedInClientCommandPacket.EnumClientCommand.OPEN_INVENTORY_ACHIEVEMENT;
import static com.ngxdev.tinyprotocol.packet.in.WrappedInEntityActionPacket.EnumPlayerAction.STOP_SPRINTING;

@CheckType(id = "inventory:g", name = "Inventory G", type = INVENTORY)
public class InventoryG extends Check {
    private boolean sent;
    private boolean vehicle;

    void check(WrappedInFlyingPacket packet) {
        if (!canCheck()) return;
        if (this.sent) {
            fail();
        }
        this.vehicle = false;
        this.sent = false;
    }

    void check(WrappedInSteerVehiclePacket packet) {
        this.vehicle = true;
    }

    void check(WrappedInEntityActionPacket packet) {
        if (packet.getAction() == STOP_SPRINTING) {
            this.sent = false;
        }
    }

    void check(WrappedInClientCommandPacket packet) {
        if (packet.getCommand() == OPEN_INVENTORY_ACHIEVEMENT) {
            if (data.timers.lastSprint.wasReset() && !this.vehicle) {
                this.sent = true;
            }
        }
    }
}
