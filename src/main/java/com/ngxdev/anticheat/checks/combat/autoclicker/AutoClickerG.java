package com.ngxdev.anticheat.checks.combat.autoclicker;

import com.ngxdev.anticheat.api.check.Check;
import api.CheckType;
import com.ngxdev.tinyprotocol.packet.in.WrappedInArmAnimationPacket;
import com.ngxdev.tinyprotocol.packet.in.WrappedInFlyingPacket;

import java.util.ArrayList;
import java.util.List;

import static api.CheckType.Type.AUTOCLICKER;

@CheckType(id = "autoclicker:g", name = "AutoClicker G", type = AUTOCLICKER)
public class AutoClickerG extends Check {
    int ticks;
    long lastClick;
    List<Boolean> longs = new ArrayList<>();
    List<Boolean> shorts = new ArrayList<>();

    void check(WrappedInFlyingPacket packet) {
        if (data.state.isDigging || data.state.isPlacing) return;

        ticks++;
        if (ticks > 3) return;
        boolean add = ticks < 3;
        if (!add) {
            longs.add(false);
            shorts.add(false);
        } else {
            shorts.add(true);
            longs.add(true);
        }
        if (longs.size() > 275) {
            if (!longs.contains(false)) {
                fail(2, 20 * 5, "S");
            }
            longs.clear();
        }
        if (shorts.size() <= 100) return;
        if (!longs.contains(false)) {
            fail(2, 20 * 5, "L");
        }
        shorts.clear();
    }

    void check(WrappedInArmAnimationPacket packet) {
        ticks = 0;
        long diff = System.currentTimeMillis() - lastClick;
        if (diff > 250) {
            if (!longs.isEmpty()) {
                longs.remove(longs.size() - 1);
                if (longs.contains(false)) {
                    longs.clear();
                }
            }
            if (!shorts.isEmpty()) {
                shorts.remove(shorts.size() - 1);
                if (shorts.contains(false)) {
                    shorts.clear();
                }
            }
        }
        lastClick = System.currentTimeMillis();
    }
}

