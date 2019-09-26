package com.ngxdev.anticheat.checks.combat.killaura;

import com.ngxdev.anticheat.api.check.Check;
import api.CheckType;
import com.ngxdev.tinyprotocol.api.NMSObject;
import com.ngxdev.tinyprotocol.packet.in.WrappedInArmAnimationPacket;
import com.ngxdev.tinyprotocol.packet.in.WrappedInEntityActionPacket;
import com.ngxdev.tinyprotocol.packet.in.WrappedInFlyingPacket;
import com.ngxdev.tinyprotocol.packet.in.WrappedInUseEntityPacket;

import static api.CheckType.Type.KILLAURA;
import static api.ConfigValueX.BLOCK_HITS_TIME;

@CheckType(id = "killaura:g", name = "KillAura G", type = KILLAURA, maxVl = 2)
public class KillAuraG extends Check {
    private int stage;

    void check(WrappedInFlyingPacket packet) {
        check0(packet);
    }

    void check(WrappedInArmAnimationPacket packet) {
        check0(packet);
    }

    void check(WrappedInEntityActionPacket packet) {
        check0(packet);
    }

    void check(WrappedInUseEntityPacket packet) {
        check0(packet);
    }

    void check0(NMSObject packet) {
        if (!canCheck()) return;
        final int calculusStage = this.stage % 6;
        if (calculusStage == 0) {
            if (packet instanceof WrappedInArmAnimationPacket) {
                ++this.stage;
            } else {
                this.stage = 0;
            }
        } else if (calculusStage == 1) {
            if (packet instanceof WrappedInUseEntityPacket) {
                ++this.stage;
            } else {
                this.stage = 0;
            }
        } else if (calculusStage == 2) {
            if (packet instanceof WrappedInEntityActionPacket) {
                ++this.stage;
            } else {
                this.stage = 0;
            }
        } else if (calculusStage == 3) {
            if (packet instanceof WrappedInFlyingPacket) {
                ++this.stage;
            } else {
                this.stage = 0;
            }
        } else if (calculusStage == 4) {
            if (packet instanceof WrappedInEntityActionPacket) {
                ++this.stage;
            } else {
                this.stage = 0;
            }
        } else if (calculusStage == 5) {
            if (packet instanceof WrappedInFlyingPacket) {
                if (++this.stage >= 30) {
                    if (fail()) data.state.cancelHits = BLOCK_HITS_TIME.asInteger();
                }
            } else {
                this.stage = 0;
            }
        }
    }
}
