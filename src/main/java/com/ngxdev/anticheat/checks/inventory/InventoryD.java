package com.ngxdev.anticheat.checks.inventory;

import com.ngxdev.anticheat.api.check.Check;
import api.CheckType;
import com.ngxdev.tinyprotocol.api.NMSObject;
import com.ngxdev.tinyprotocol.packet.in.WrappedInClientCommandPacket;
import com.ngxdev.tinyprotocol.packet.in.WrappedInFlyingPacket;

import static api.CheckType.Type.INVENTORY;

@CheckType(id = "inventory:d", name = "Inventory D", type = INVENTORY)
public class InventoryD extends Check {
    private int stage;

    void check(WrappedInFlyingPacket packet) {
        check0(packet);
    }

    void check(WrappedInClientCommandPacket packet) {
        check0(packet);
    }

    void check0(NMSObject packet) {
        if (!canCheck()) return;
        if (this.stage == 0) {
            if (packet instanceof WrappedInClientCommandPacket && ((WrappedInClientCommandPacket) packet).getCommand() == WrappedInClientCommandPacket.EnumClientCommand.OPEN_INVENTORY_ACHIEVEMENT) {
                ++this.stage;
            }
        } else if (this.stage == 1) {
            if (packet instanceof WrappedInFlyingPacket && ((WrappedInFlyingPacket) packet).isLook()) {
                ++this.stage;
            } else {
                this.stage = 0;
            }
        } else if (this.stage == 2) {
            if (packet instanceof WrappedInFlyingPacket && ((WrappedInFlyingPacket) packet).isLook()) {
                fail();
            }
            this.stage = 0;
        }
    }
}
