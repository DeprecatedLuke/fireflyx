package com.ngxdev.anticheat.checks.inventory;

import com.ngxdev.anticheat.api.check.Check;
import api.CheckType;
import com.ngxdev.anticheat.data.TimedLocation;
import com.ngxdev.tinyprotocol.packet.in.WrappedInWindowClickPacket;

import java.util.LinkedList;

import static api.CheckType.Type.INVENTORY;

@CheckType(id = "inventory:c", name = "Inventory C", type = INVENTORY)
public class InventoryC extends Check {
    private final LinkedList<Long> delays = new LinkedList<>();
    double vl;

    void check(WrappedInWindowClickPacket packet) {
        if (!canCheck()) return;
        TimedLocation lastMovePacket = data.locations.getLast().getX();
        if (lastMovePacket == null) return;
        long delay = System.currentTimeMillis() - lastMovePacket.getTime();
        this.delays.add(delay);
        if (this.delays.size() == 10) {
            double average = 0.0;
            for (long loopDelay : this.delays) {
                average += loopDelay;
            }
            average /= this.delays.size();
            this.delays.clear();
            if (average <= 35.0) {
                if ((vl += 1.25) >= 4.0) {
                    fail(2, 20 * 60, "a=%.1f,d=%.2f", average, vl);
                    vl = 0.0;
                }
            } else {
                vl -= 0.5;
            }
        }
    }
}
