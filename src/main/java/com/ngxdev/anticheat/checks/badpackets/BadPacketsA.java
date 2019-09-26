package com.ngxdev.anticheat.checks.badpackets;

import com.ngxdev.anticheat.api.check.Check;
import api.CheckType;
import com.ngxdev.tinyprotocol.api.ProtocolVersion;
import com.ngxdev.tinyprotocol.packet.in.WrappedInFlyingPacket;
import com.ngxdev.tinyprotocol.packet.in.WrappedInSteerVehiclePacket;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerGameModeChangeEvent;

import static api.CheckType.Type.BADPACKET;

@CheckType(id = "badpackets:a", name = "BadPackets A", type = BADPACKET)
public class BadPacketsA extends Check {
    int streak;

    @EventHandler
    void onEvent(PlayerGameModeChangeEvent e) {
        streak = 0;
    }

    void check(WrappedInFlyingPacket packet) {
        if (!canCheck() || data.protocolVersion.isAbove(ProtocolVersion.V1_8_9)) return;
        if (packet.isPos()) {
            this.streak = 0;
        } else if (++this.streak > 20) {
            if (fail()) setback();
        }
    }

    void check(WrappedInSteerVehiclePacket packet) {
        this.streak = 0;
    }
}
