package com.ngxdev.anticheat.checks.inventory;

import com.ngxdev.anticheat.api.check.Check;
import api.CheckType;
import com.ngxdev.tinyprotocol.api.NMSObject;
import com.ngxdev.tinyprotocol.packet.in.WrappedInArmAnimationPacket;
import com.ngxdev.tinyprotocol.packet.in.WrappedInEntityActionPacket;

import static api.CheckType.Type.INVENTORY;
import static com.ngxdev.tinyprotocol.packet.in.WrappedInEntityActionPacket.EnumPlayerAction.START_SPRINTING;

@CheckType(id = "inventory:b", name = "Inventory B", type = INVENTORY, maxVl = 2, timeout = 20 * 60)
public class InventoryB extends Check {
    void check(WrappedInEntityActionPacket packet) {
        check0(packet);
    }

    void check(WrappedInArmAnimationPacket packet) {
        check0(packet);
    }

    void check0(NMSObject packet) {
        if (!canCheck()) return;
        if (((packet instanceof WrappedInEntityActionPacket && ((WrappedInEntityActionPacket) packet).getAction() == START_SPRINTING) || packet instanceof WrappedInArmAnimationPacket) && data.state.isInventoryOpen) {
            fail();
            data.state.isInventoryOpen = false;
        }
    }
}
