package com.ngxdev.anticheat.checks.combat.autoclicker.experimental;

import com.ngxdev.anticheat.api.check.Check;
import api.CheckType;
import com.ngxdev.tinyprotocol.packet.in.WrappedInArmAnimationPacket;
import com.ngxdev.tinyprotocol.packet.in.WrappedInFlyingPacket;
import com.ngxdev.anticheat.utils.Greek;

import static api.CheckType.State.EXPERIMENTAL;
import static api.CheckType.Type.AUTOCLICKER;

@CheckType(id = "autoclicker:5", name = "AutoClicker " + Greek.FIVE, type = AUTOCLICKER, state = EXPERIMENTAL)
public class AutoClicker5 extends Check {
    private boolean failed;
    private boolean sent;
    private int count;
    private int vl;

    void check(WrappedInFlyingPacket packet) {
        this.failed = false;
        this.sent = false;
        this.count = 0;
    }


    void check(WrappedInArmAnimationPacket packet) {
        if (!canCheck()) return;
        if (!data.state.isDigging && !data.state.isPlacing) {
            if (this.sent) {
                ++this.count;
                if (!this.failed) {
                    if (++vl >= 5) {
                        fail("c=%d", count);
                        vl = 0;
                    }
                    this.failed = true;
                }
            } else {
                this.sent = true;
                this.count = 0;
            }
        }
    }
}
