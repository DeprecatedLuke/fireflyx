package com.ngxdev.anticheat.checks.inventory;

import com.ngxdev.anticheat.api.check.Check;
import com.ngxdev.anticheat.api.check.Priority;
import api.CheckType;
import com.ngxdev.tinyprotocol.packet.in.WrappedInWindowClickPacket;

import static api.CheckType.Type.INVENTORY;

@CheckType(id = "inventory:a", name = "Inventory A", type = INVENTORY, maxVl = 6)
public class InventoryA extends Check {
    @Priority(50)
    void check(WrappedInWindowClickPacket packet) {
        if (!canCheck()) return;
        if (packet.getId() == 0 && !data.state.isInventoryOpen) {
            fail();
            data.state.isInventoryOpen = true;
        }
    }
}
