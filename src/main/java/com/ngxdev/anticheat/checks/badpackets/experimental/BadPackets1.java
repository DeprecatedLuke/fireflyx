package com.ngxdev.anticheat.checks.badpackets.experimental;

import com.ngxdev.anticheat.api.check.Check;
import api.CheckType;
import com.ngxdev.tinyprotocol.packet.in.WrappedInBlockPlacePacket;
import com.ngxdev.tinyprotocol.packet.in.WrappedInEntityActionPacket;
import com.ngxdev.tinyprotocol.packet.in.WrappedInFlyingPacket;
import com.ngxdev.tinyprotocol.packet.in.WrappedInSteerVehiclePacket;
import org.bukkit.inventory.ItemStack;

import static api.CheckType.State.EXPERIMENTAL;
import static api.CheckType.Type.BADPACKET;

@CheckType(id = "badpackets:1", name = "BadPackets 1", type = BADPACKET, state = EXPERIMENTAL)
public class BadPackets1 extends Check {
    private boolean sent;
    private boolean vehicle;

    void check(WrappedInFlyingPacket packet) {
        if (!canCheck()) return;
        if (this.sent) if (fail()) setback();

        this.vehicle = false;
        this.sent = false;
    }

    void check(WrappedInBlockPlacePacket packet) {
        if (packet.getFace() == 255) {
            ItemStack itemStack = packet.getItemStack();
            if (itemStack != null && itemStack.getType().name().toLowerCase().contains("sword") && data.timers.lastSprint.wasReset() && !this.vehicle) {
                this.sent = true;
            }
        }
    }

    void check(WrappedInEntityActionPacket packet) {
        this.sent = false;
    }

    void check(WrappedInSteerVehiclePacket packet) {
        this.vehicle = true;
    }
}
