package com.ngxdev.anticheat.checks.movement.wtap;

import com.ngxdev.anticheat.api.check.Check;
import api.CheckType;
import com.ngxdev.tinyprotocol.packet.in.WrappedInBlockPlacePacket;
import com.ngxdev.tinyprotocol.packet.in.WrappedInEntityActionPacket;
import com.ngxdev.tinyprotocol.packet.in.WrappedInFlyingPacket;
import com.ngxdev.tinyprotocol.packet.in.WrappedInUseEntityPacket;

import java.util.LinkedList;

import static api.CheckType.Type.MOVEMENT;
import static com.ngxdev.tinyprotocol.packet.in.WrappedInEntityActionPacket.EnumPlayerAction.STOP_SPRINTING;
import static com.ngxdev.tinyprotocol.packet.in.WrappedInUseEntityPacket.EnumEntityUseAction.ATTACK;

@CheckType(id = "wtap:b", name = "WTap B", type = MOVEMENT)
public class WTapB extends Check {
    private LinkedList<Integer> recentCounts = new LinkedList<>();
    private boolean block;
    private int flyingCount;
    private double vl;


    void check(WrappedInFlyingPacket packet) {
        ++this.flyingCount;
        this.block = false;
    }

    void check(WrappedInBlockPlacePacket packet) {
        this.block = true;
    }

    void check(WrappedInUseEntityPacket packet) {
        if (packet.getAction() == ATTACK) {
            this.flyingCount = 0;
        }
    }

    void check(WrappedInEntityActionPacket packet) {
        if (packet.getAction() == STOP_SPRINTING && data.timers.lastAttack.hasNotPassed(20) && this.flyingCount < 10 && !this.block) {
            this.recentCounts.add(this.flyingCount);
            if (!canCheckMovement()
                    || !isMoving()) return;
            if (this.recentCounts.size() == 20) {
                double average = 0.0;
                for (double flyingCount : this.recentCounts) {
                    average += flyingCount;
                }
                average /= this.recentCounts.size();
                double stdDev = 0.0;
                for (long l : this.recentCounts) {
                    stdDev += Math.pow(l - average, 2.0);
                }
                stdDev /= this.recentCounts.size();
                stdDev = Math.sqrt(stdDev);
                if (stdDev < 0.3) {
                    if ((vl += 1.2) >= 2.4) {
                        fail("s=%.2f,d=%.2f", stdDev, vl);
                    }
                } else {
                    vl -= 2.0;
                }
                this.recentCounts.clear();
            }
        }
    }
}
