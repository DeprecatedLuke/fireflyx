package com.ngxdev.anticheat.checks.world.scaffold;

import com.ngxdev.anticheat.api.check.Check;
import api.CheckType;
import com.ngxdev.tinyprotocol.api.NMSObject;
import com.ngxdev.tinyprotocol.packet.in.WrappedInArmAnimationPacket;
import com.ngxdev.tinyprotocol.packet.in.WrappedInBlockPlacePacket;
import com.ngxdev.tinyprotocol.packet.in.WrappedInFlyingPacket;

import static api.CheckType.Type.WORLD;

@CheckType(id = "scaffold:c", name = "Scaffold C", type = WORLD)
public class ScaffoldC extends Check {
    private int looks;
    private int stage;
    private double vl;

    void check(WrappedInFlyingPacket packet) {
        if (!canCheck()) return;
        check0(packet);
    }

    void check(WrappedInBlockPlacePacket packet) {
        check0(packet);
    }

    void check(WrappedInArmAnimationPacket packet) {
        check0(packet);
    }

    void check0(NMSObject packet) {
        if (packet instanceof WrappedInFlyingPacket && ((WrappedInFlyingPacket) packet).isLook()) {
            if (this.stage == 0) {
                ++this.stage;
            } else if (this.stage == 4) {
                if ((vl += 1.75) > 3.5) {
                    fail("v=%.2f", vl);
                }
                this.stage = 0;
            } else {
                this.looks = 0;
                this.stage = 0;
                vl -= 0.2;
            }
        } else if (packet instanceof WrappedInBlockPlacePacket) {
            if (this.stage == 1) {
                ++this.stage;
            } else {
                this.looks = 0;
                this.stage = 0;
            }
        } else if (packet instanceof WrappedInArmAnimationPacket) {
            if (this.stage == 2) {
                ++this.stage;
            } else {
                this.looks = 0;
                this.stage = 0;
                vl -= 0.2;
            }
        } else if (packet instanceof WrappedInFlyingPacket && ((WrappedInFlyingPacket) packet).isPos()) {
            if (this.stage == 3) {
                if (++this.looks == 3) {
                    this.stage = 4;
                    this.looks = 0;
                }
            } else {
                this.looks = 0;
                this.stage =0;
            }
        }
    }
}
