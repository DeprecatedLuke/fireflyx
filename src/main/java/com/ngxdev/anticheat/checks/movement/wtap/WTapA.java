package com.ngxdev.anticheat.checks.movement.wtap;

import com.ngxdev.anticheat.api.check.Check;
import api.CheckType;
import com.ngxdev.tinyprotocol.packet.in.WrappedInBlockDigPacket;
import com.ngxdev.tinyprotocol.packet.in.WrappedInEntityActionPacket;
import com.ngxdev.tinyprotocol.packet.in.WrappedInFlyingPacket;

import java.util.LinkedList;

import static api.CheckType.Type.MOVEMENT;
import static com.ngxdev.tinyprotocol.packet.in.WrappedInBlockDigPacket.EnumPlayerDigType.RELEASE_USE_ITEM;
import static com.ngxdev.tinyprotocol.packet.in.WrappedInEntityActionPacket.EnumPlayerAction.START_SPRINTING;
import static com.ngxdev.tinyprotocol.packet.in.WrappedInEntityActionPacket.EnumPlayerAction.STOP_SPRINTING;

@CheckType(id = "wtap:a", name = "WTap A", type = MOVEMENT)
public class WTapA extends Check {
    private LinkedList<Integer> recentCounts = new LinkedList<>();
    private boolean release;
    private int flyingCount;
    private double vl;

    void check(WrappedInFlyingPacket packet) {
        ++this.flyingCount;
        this.release = false;
    }

    void check(WrappedInBlockDigPacket packet) {
        if (packet.getAction() == RELEASE_USE_ITEM) {
            this.release = true;
        }
    }

    void check(WrappedInEntityActionPacket packet) {
        if (packet.getAction() == START_SPRINTING) {
            if (data.timers.lastAttack.hasNotPassed(20) && this.flyingCount < 10 && !this.release) {
                if (!canCheckMovement()
                        || !isMoving()) return;
                this.recentCounts.add(this.flyingCount);
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
                    if (stdDev == 0.0) {
                        if ((vl += 1.2) >= 2.4) {
                            fail("s=%.2f,d=%.2f", stdDev, vl);
                        }
                    } else {
                        vl -= 2.0;
                    }
                    this.recentCounts.clear();
                }
            }
        } else if (packet.getAction() == STOP_SPRINTING) {
            this.flyingCount = 0;
        }
    }
}
