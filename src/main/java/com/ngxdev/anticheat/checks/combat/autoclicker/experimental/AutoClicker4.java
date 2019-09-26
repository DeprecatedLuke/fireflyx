package com.ngxdev.anticheat.checks.combat.autoclicker.experimental;

import com.ngxdev.anticheat.api.check.Check;
import api.CheckType;
import com.ngxdev.tinyprotocol.packet.in.WrappedInBlockPlacePacket;
import com.ngxdev.tinyprotocol.packet.in.WrappedInFlyingPacket;
import com.ngxdev.anticheat.utils.Greek;

import static api.CheckType.State.EXPERIMENTAL;
import static api.CheckType.Type.AUTOCLICKER;

@CheckType(id = "autoclicker:4", name = "AutoClicker " + Greek.FOUR, type = AUTOCLICKER, state = EXPERIMENTAL)
public class AutoClicker4 extends Check {
    private boolean failed;
    private boolean sent;

    void check(WrappedInFlyingPacket packet) {
        this.failed = false;
        this.sent = false;
    }

    void check(WrappedInBlockPlacePacket packet) {
        if (!canCheck()) return;
        if (packet.getFace() == 255) {
            if (this.sent) {
                if (!this.failed) {
                    fail();
                    this.failed = true;
                }
            } else {
                this.sent = true;
            }
        }
    }
}
