package com.ngxdev.anticheat.checks.combat.autoclicker.experimental;

import com.ngxdev.anticheat.api.check.Check;
import api.CheckType;
import com.ngxdev.tinyprotocol.packet.in.WrappedInArmAnimationPacket;
import com.ngxdev.tinyprotocol.packet.in.WrappedInBlockDigPacket;
import com.ngxdev.tinyprotocol.packet.in.WrappedInFlyingPacket;
import com.ngxdev.anticheat.utils.Greek;

import static api.CheckType.State.EXPERIMENTAL;
import static api.CheckType.Type.AUTOCLICKER;
import static com.ngxdev.tinyprotocol.packet.in.WrappedInBlockDigPacket.EnumPlayerDigType.RELEASE_USE_ITEM;

@CheckType(id = "autoclicker:6", name = "AutoClicker " + Greek.SIX, type = AUTOCLICKER, state = EXPERIMENTAL)
public class AutoClicker6 extends Check {
    private int clicks;
    private int outliers;
    private int flyingCount;
    private boolean release;
    private double vl;

    void check(WrappedInFlyingPacket packet) {
        ++this.flyingCount;
    }

    void check(WrappedInBlockDigPacket packet) {
        if (packet.getAction() == RELEASE_USE_ITEM) {
            this.release = true;
        }
    }

    void check(WrappedInArmAnimationPacket packet) {
        if (!canCheck()) return;
        if (!data.state.isDigging && !data.state.isPlacing) {
            if (this.flyingCount < 10) {
                if (this.release) {
                    this.release = false;
                    this.flyingCount = 0;
                    return;
                }
                if (this.flyingCount > 3) {
                    ++this.outliers;
                } else if (this.flyingCount == 0) {
                    return;
                }
                if (++this.clicks == 40) {
                    if (this.outliers == 0) {
                        if ((vl += 1.4) >= 4.0) {
                            fail("o=%s,d=%.2f", this.outliers, vl);
                        }
                    } else {
                        vl -= 0.8;
                    }
                    this.outliers = 0;
                    this.clicks = 0;
                }
            }
            this.flyingCount = 0;
        }
    }
}
