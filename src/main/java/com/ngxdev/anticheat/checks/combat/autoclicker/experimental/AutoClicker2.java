package com.ngxdev.anticheat.checks.combat.autoclicker.experimental;

import com.ngxdev.anticheat.api.check.Check;
import api.CheckType;
import com.ngxdev.tinyprotocol.packet.in.WrappedInBlockDigPacket;
import com.ngxdev.tinyprotocol.packet.in.WrappedInBlockPlacePacket;
import com.ngxdev.tinyprotocol.packet.in.WrappedInFlyingPacket;
import com.ngxdev.anticheat.utils.Greek;

import java.util.LinkedList;

import static api.CheckType.State.EXPERIMENTAL;
import static api.CheckType.Type.AUTOCLICKER;
import static com.ngxdev.tinyprotocol.packet.in.WrappedInBlockDigPacket.EnumPlayerDigType.RELEASE_USE_ITEM;

@CheckType(id = "autoclicker:2", name = "AutoClicker " + Greek.TWO, type = AUTOCLICKER, state = EXPERIMENTAL)
public class AutoClicker2 extends Check {
    private final LinkedList<Integer> recentCounts = new LinkedList<>();
    private int flyingCount;
    private double vl;

    void check(WrappedInFlyingPacket packet) {
        ++this.flyingCount;
    }

    void check(WrappedInBlockPlacePacket packet) {
        if (packet.getItemStack() != null && packet.getItemStack().getType().name().toLowerCase().startsWith("sword")) {
            this.flyingCount = 0;
        }
    }

    void check(WrappedInBlockDigPacket packet) {
        if (!canCheck()) return;
        if (packet.getAction() == RELEASE_USE_ITEM) {
            if (this.flyingCount < 10 && data.timers.lastArmAnimation.hasNotPassed(20 * 2)) {
                this.recentCounts.add(this.flyingCount);
                if (this.recentCounts.size() == 100) {
                    double average = 0.0;
                    for (final double flyingCount : this.recentCounts) {
                        average += flyingCount;
                    }
                    average /= this.recentCounts.size();
                    double stdDev = 0.0;
                    for (final long l : this.recentCounts) {
                        stdDev += Math.pow(l - average, 2.0);
                    }
                    stdDev /= this.recentCounts.size();
                    stdDev = Math.sqrt(stdDev);
                    if (stdDev < 0.2) {
                        if ((vl += 1.4) >= 4.0) {
                            fail("s=%.2f,d=%.2f", stdDev, vl);
                        }
                    } else {
                        vl -= 0.8;
                    }
                    this.recentCounts.clear();
                }
            }
        }
    }
}
