package com.ngxdev.anticheat.checks.combat.autoclicker;

import com.ngxdev.anticheat.api.check.Check;
import api.CheckType;
import com.ngxdev.tinyprotocol.packet.in.WrappedInArmAnimationPacket;
import com.ngxdev.tinyprotocol.packet.in.WrappedInBlockDigPacket;

import static api.CheckType.Type.AUTOCLICKER;


@CheckType(id = "autoclicker:b", name = "AutoClicker B", type = AUTOCLICKER)
public class AutoClickerB extends Check {
    private boolean sent;
    private int vl;

    void check(WrappedInArmAnimationPacket packet) {
        this.sent = false;
    }

    void check(WrappedInBlockDigPacket packet) {
        if (!canCheck()) return;
        WrappedInBlockDigPacket.EnumPlayerDigType digType = packet.getAction();
        if (digType == WrappedInBlockDigPacket.EnumPlayerDigType.START_DESTROY_BLOCK) {
            this.sent = true;
        } else if (digType == WrappedInBlockDigPacket.EnumPlayerDigType.ABORT_DESTROY_BLOCK) {
            if (this.sent) {
                if (++vl > 10 && vl >= 20) {
                    fail("v=%d", vl);
                }
            } else {
                vl = 0;
            }
        }
    }
}
