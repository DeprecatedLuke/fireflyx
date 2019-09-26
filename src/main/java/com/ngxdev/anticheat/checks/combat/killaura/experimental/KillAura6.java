package com.ngxdev.anticheat.checks.combat.killaura.experimental;

import com.ngxdev.anticheat.api.check.Check;
import api.CheckType;
import com.ngxdev.anticheat.data.TimedLocation;
import com.ngxdev.tinyprotocol.packet.in.WrappedInFlyingPacket;
import com.ngxdev.tinyprotocol.packet.in.WrappedInUseEntityPacket;

import static api.CheckType.Type.KILLAURA;
import static api.ConfigValueX.BLOCK_HITS_TIME;

@CheckType(id = "killaura:6", name = "KillAura 6", type = KILLAURA)
public class KillAura6 extends Check {
    private long lastAttack;
    private boolean attack;
    private double vl;

    void check(WrappedInFlyingPacket packet) {
        if (!canCheck()) return;
        if (this.attack) {
            long time = System.currentTimeMillis() - this.lastAttack;
            if (time >= 25L) {
                if (++vl >= 10.0) {
                    if (fail("t=%s,d=%.1f", time, vl)) data.state.cancelHits = BLOCK_HITS_TIME.asInteger();
                }
            } else {
                vl -= 0.25;
            }
            this.attack = false;
        }
    }

    void check(WrappedInUseEntityPacket packet) {
        synchronized (data.locations) {
            if (data.locations.isEmpty() || !canCheck()) return;

            TimedLocation lastMovePacket = data.locations.getLast().getX();
            if (lastMovePacket == null) return;

            long delay = System.currentTimeMillis() - lastMovePacket.getTime();
            if (delay <= 25.0) {
                this.lastAttack = System.currentTimeMillis();
                this.attack = true;
            } else {
                vl -= 0.25;
            }
        }
    }
}
