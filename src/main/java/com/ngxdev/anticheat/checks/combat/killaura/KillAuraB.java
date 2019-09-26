package com.ngxdev.anticheat.checks.combat.killaura;

import com.ngxdev.anticheat.api.check.Check;
import api.CheckType;
import com.ngxdev.tinyprotocol.packet.in.WrappedInBlockPlacePacket;
import com.ngxdev.tinyprotocol.packet.in.WrappedInFlyingPacket;
import com.ngxdev.tinyprotocol.packet.in.WrappedInUseEntityPacket;

import static api.CheckType.Type.KILLAURA;

@CheckType(id = "killaura:b", name = "KillAura B", type = KILLAURA, maxVl = 3)
public class KillAuraB extends Check {
    private boolean sentUseEntity;

    void check(WrappedInFlyingPacket packet) {
        this.sentUseEntity = false;
    }

    void check(WrappedInUseEntityPacket packet) {
        this.sentUseEntity = true;
    }

    void check(WrappedInBlockPlacePacket packet) {
        if (!canCheck()) return;
        if (packet.getFace() != 255 && this.sentUseEntity) {
            if (fail()) packet.setCancelled(true);
        }
    }
}
