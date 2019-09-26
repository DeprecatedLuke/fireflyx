package com.ngxdev.anticheat.checks.combat.killaura.experimental;

import com.ngxdev.anticheat.api.check.Check;
import api.CheckType;
import com.ngxdev.tinyprotocol.api.NMSObject;
import com.ngxdev.tinyprotocol.packet.in.WrappedInArmAnimationPacket;
import com.ngxdev.tinyprotocol.packet.in.WrappedInFlyingPacket;
import com.ngxdev.tinyprotocol.packet.in.WrappedInUseEntityPacket;

import static api.CheckType.State.EXPERIMENTAL;
import static api.CheckType.Type.KILLAURA;

@CheckType(id = "killaura:2", name = "KillAura 2", type = KILLAURA, state = EXPERIMENTAL)
public class KillAura2 extends Check {
    private int ticksSinceStage;
    private int streak;
    private int stage;

    void check(WrappedInFlyingPacket packet) {
        check0(packet);
    }

    void check(WrappedInArmAnimationPacket packet) {
        check0(packet);
    }

    void check(WrappedInUseEntityPacket packet) {
        check0(packet);
    }

    void check0(NMSObject packet) {
        if (!canCheck()) return;
        if (packet instanceof WrappedInArmAnimationPacket) {
            if (this.stage == 0) {
                this.stage = 1;
            } else {
                this.stage = 0;
                this.streak = 0;
            }
        } else if (packet instanceof WrappedInUseEntityPacket) {
            if (this.stage == 1) {
                ++this.stage;
            } else {
                this.stage = 0;
            }
        } else if (packet instanceof WrappedInFlyingPacket && ((WrappedInFlyingPacket) packet).isPos() && ((WrappedInFlyingPacket) packet).isLook()) {
            if (this.stage == 2) {
                ++this.stage;
            } else {
                this.stage = 0;
            }
        } else if (packet instanceof WrappedInFlyingPacket && ((WrappedInFlyingPacket) packet).isPos()) {
            if (this.stage == 3) {
                if (++this.streak >= 15) {
                    fail("s=%s", this.streak);
                }
                this.ticksSinceStage = 0;
            }
            this.stage = 0;
        }
        if (packet instanceof WrappedInFlyingPacket && ++this.ticksSinceStage > 40) {
            this.streak = 0;
        }
    }
}
