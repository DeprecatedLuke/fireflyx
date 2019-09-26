package com.ngxdev.anticheat.checks.combat.autoclicker.experimental;

import com.ngxdev.anticheat.api.check.Check;
import api.CheckType;
import com.ngxdev.tinyprotocol.packet.in.WrappedInArmAnimationPacket;
import com.ngxdev.tinyprotocol.packet.in.WrappedInBlockDigPacket;
import com.ngxdev.tinyprotocol.packet.in.WrappedInFlyingPacket;
import com.ngxdev.anticheat.utils.Greek;

import static api.CheckType.State.EXPERIMENTAL;
import static api.CheckType.Type.AUTOCLICKER;

@CheckType(id = "autoclicker:0", name = "AutoClicker " + Greek.ZERO, type = AUTOCLICKER, state = EXPERIMENTAL)
public class AutoClicker0 extends Check {
    private int movements;
    private int failed;
    private int passed;
    private int stage;
    private double vl;

    void check(WrappedInFlyingPacket packet) {
        if (this.stage == 2) {
            ++this.stage;
        } else {
            this.stage = 0;
        }
        ++this.movements;
    }

    void check(WrappedInArmAnimationPacket packet){
        if (this.stage == 0 || this.stage == 1) {
            ++this.stage;
        } else {
            this.stage = 1;
        }
    }

    void check(WrappedInBlockDigPacket packet) {
        if (!canCheck()) return;
        if (this.stage == 3) {
            ++this.failed;
        } else {
            ++this.passed;
        }
        if (this.movements >= 200 && this.failed + this.passed > 60) {
            final double rat = (this.passed == 0) ? -1.0 : (this.failed / this.passed);
            if (rat > 2.5) {
                if ((vl += 1.0 + (rat - 2.0) * 0.75) >= 4.0) {
                    fail("r=%.2f,d=%.2f", rat, vl);
                }
            } else {
                vl -= 2.0;
            }
            this.movements = 0;
            this.passed = 0;
            this.failed = 0;
        }
    }
}
