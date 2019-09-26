package com.ngxdev.anticheat.checks.combat.autoclicker;

import com.ngxdev.anticheat.api.check.Check;
import api.CheckType;
import com.ngxdev.tinyprotocol.packet.in.WrappedInBlockDigPacket;
import com.ngxdev.tinyprotocol.packet.in.WrappedInFlyingPacket;
import com.ngxdev.tinyprotocol.packet.types.BaseBlockPosition;

import java.util.LinkedList;

import static api.CheckType.Type.AUTOCLICKER;
import static com.ngxdev.tinyprotocol.packet.in.WrappedInBlockDigPacket.EnumPlayerDigType.ABORT_DESTROY_BLOCK;
import static com.ngxdev.tinyprotocol.packet.in.WrappedInBlockDigPacket.EnumPlayerDigType.START_DESTROY_BLOCK;

@CheckType(id = "autoclicker:f", name = "AutoClicker F", type = AUTOCLICKER)
public class AutoClickerF extends Check {
    private final LinkedList<Integer> recentCounts = new LinkedList<>();
    private BaseBlockPosition lastBlock;
    private int flyingCount;
    private double vl;

    void check(WrappedInFlyingPacket packet) {
        ++this.flyingCount;
    }

    void check(WrappedInBlockDigPacket packet) {
        if (!canCheck()) return;
        if (packet.getAction() == START_DESTROY_BLOCK) {
            if (this.lastBlock != null && this.lastBlock.equals(packet.getBlockPosition())) {
                this.recentCounts.addLast(this.flyingCount);
                if (this.recentCounts.size() == 20) {
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
                    if (stdDev < 0.45 && ++vl >= 3.0) {
                        fail("s=%.2f,d=%.1f", stdDev, vl);
                    } else {
                        vl -= 0.5;
                    }
                    this.recentCounts.clear();
                }
            }
            this.flyingCount = 0;
        } else if (packet.getAction() == ABORT_DESTROY_BLOCK) {
            this.lastBlock = packet.getBlockPosition();
        }
    }
}
