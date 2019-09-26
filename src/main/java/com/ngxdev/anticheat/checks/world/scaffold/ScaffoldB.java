package com.ngxdev.anticheat.checks.world.scaffold;

import com.ngxdev.anticheat.api.check.Check;
import api.CheckType;
import com.ngxdev.anticheat.data.TimedLocation;
import com.ngxdev.tinyprotocol.packet.in.WrappedInBlockPlacePacket;
import com.ngxdev.tinyprotocol.packet.in.WrappedInFlyingPacket;

import static api.CheckType.Type.WORLD;

@CheckType(id = "scaffold:b", name = "Scaffold B", type = WORLD)
public class ScaffoldB extends Check {
    private long lastPlace;
    private boolean place;
    private double vl;

    void check(WrappedInFlyingPacket packet) {
        if (!canCheck()) return;
        if (this.place) {
            long time = System.currentTimeMillis() - this.lastPlace;
            if (time >= 25L) {
                if (++vl >= 10.0) {
                    fail();
                }
            } else {
                vl -= 0.25;
            }
            this.place = false;
        }
    }

    void check(WrappedInBlockPlacePacket packet) {
        if (data.locations.isEmpty()) return;
        TimedLocation lastMovePacket = data.locations.getLast().getX();
        if (lastMovePacket == null) {
            return;
        }
        final long delay = System.currentTimeMillis() - lastMovePacket.getTime();
        if (delay <= 25.0) {
            this.lastPlace = System.currentTimeMillis();
            this.place = true;
        } else {
            vl -= 0.25;
        }
    }
}
