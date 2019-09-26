package com.ngxdev.anticheat.checks.combat.killaura;

import com.ngxdev.anticheat.api.check.Check;
import api.CheckType;
import com.ngxdev.tinyprotocol.packet.in.WrappedInBlockPlacePacket;
import com.ngxdev.tinyprotocol.packet.in.WrappedInFlyingPacket;
import com.ngxdev.tinyprotocol.packet.in.WrappedInUseEntityPacket;

import static api.CheckType.Type.KILLAURA;
import static api.ConfigValueX.BLOCK_HITS_TIME;

@CheckType(id = "killaura:c", name = "KillAura C", type = KILLAURA, maxVl = 3)
public class KillAuraC extends Check {
    private boolean sentAttack;
    private boolean sentInteract;

    void check(WrappedInFlyingPacket packet) {
        this.sentInteract = false;
        this.sentAttack = false;
    }

    void check(WrappedInUseEntityPacket packet) {
        WrappedInUseEntityPacket.EnumEntityUseAction action = packet.getAction();
        if (action == WrappedInUseEntityPacket.EnumEntityUseAction.ATTACK) {
            this.sentAttack = true;
        } else if (action == WrappedInUseEntityPacket.EnumEntityUseAction.INTERACT) {
            this.sentInteract = true;
        }
    }

    void check(WrappedInBlockPlacePacket packet) {
        if (!canCheck()) return;
        if (this.sentAttack && !this.sentInteract) {
            if (fail()) data.state.cancelHits = BLOCK_HITS_TIME.asInteger();
        }
    }
}
