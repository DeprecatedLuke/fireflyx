package com.ngxdev.anticheat.checks.combat.autoclicker.experimental;

import com.ngxdev.anticheat.api.check.Check;
import api.CheckType;
import com.ngxdev.tinyprotocol.packet.in.WrappedInArmAnimationPacket;
import com.ngxdev.tinyprotocol.packet.in.WrappedInBlockDigPacket;
import com.ngxdev.tinyprotocol.packet.in.WrappedInFlyingPacket;
import com.ngxdev.anticheat.utils.Greek;

import java.util.LinkedList;

import static api.CheckType.State.EXPERIMENTAL;
import static api.CheckType.Type.AUTOCLICKER;
import static com.ngxdev.tinyprotocol.packet.in.WrappedInBlockDigPacket.EnumPlayerDigType.RELEASE_USE_ITEM;

@CheckType(id = "autoclicker:3", name = "AutoClicker " + Greek.THREE, type = AUTOCLICKER, state = EXPERIMENTAL)
public class AutoClicker3 extends Check {
    private final LinkedList<Integer> recentCounts = new LinkedList<>();
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
        if (!data.state.isPlacing && !data.state.isDigging) {
            if (this.flyingCount < 10) {
                if (this.release) {
                    this.release = false;
                    this.flyingCount = 0;
                    return;
                }
                this.recentCounts.add(this.flyingCount);
                if (this.recentCounts.size() == 100) {
                    double average = 0.0;
                    for (final int i : this.recentCounts) {
                        average += i;
                    }
                    average /= this.recentCounts.size();
                    double stdDev = 0.0;
                    for (final int j : this.recentCounts) {
                        stdDev += Math.pow(j - average, 2.0);
                    }
                    stdDev /= this.recentCounts.size();
                    stdDev = Math.sqrt(stdDev);
                    if (stdDev < 0.45) {
                        if ((vl += 1.4) >= 4.0) {
                            fail("s=%.2f,d=%.2f", stdDev, vl);
                        }
                    } else {
                        vl -= 0.8;
                    }
                    this.recentCounts.clear();
                }
            }
            this.flyingCount = 0;
        }
    }
}
