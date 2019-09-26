package com.ngxdev.anticheat.checks.badpackets.experimental;

import com.ngxdev.anticheat.api.check.Check;
import api.CheckType;
import com.ngxdev.tinyprotocol.packet.in.WrappedInFlyingPacket;
import com.ngxdev.tinyprotocol.packet.in.WrappedInSteerVehiclePacket;

import static api.CheckType.State.EXPERIMENTAL;
import static api.CheckType.Type.BADPACKET;

@CheckType(id = "badpackets:0", name = "BadPackets 0", type = BADPACKET, state = EXPERIMENTAL)
public class BadPackets0 extends Check {
    private float lastYaw;
    private float lastPitch;
    private boolean ignore;

    void check(WrappedInFlyingPacket packet) {
        if (!canCheck()) return;
        if (!packet.isPos() && packet.isLook()) {
            if (this.lastYaw == packet.getYaw() && this.lastPitch == packet.getPitch()) {
                if (!this.ignore) {
                    if (fail()) setback();
                }
                this.ignore = false;
            }
            this.lastYaw = packet.getYaw();
            this.lastPitch = packet.getPitch();
        } else {
            this.ignore = true;
        }
    }

    void check(WrappedInSteerVehiclePacket packet) {
        this.ignore = true;
    }
}
