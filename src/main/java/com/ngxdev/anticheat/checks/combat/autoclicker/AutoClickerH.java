package com.ngxdev.anticheat.checks.combat.autoclicker;

import com.ngxdev.anticheat.api.check.Check;
import api.CheckType;
import com.ngxdev.tinyprotocol.packet.in.WrappedInArmAnimationPacket;
import com.ngxdev.tinyprotocol.packet.in.WrappedInFlyingPacket;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static api.CheckType.Type.AUTOCLICKER;

@CheckType(id = "autoclicker:h", name = "AutoClicker H", type = AUTOCLICKER)
public class AutoClickerH extends Check {
    long lastArmAnimation;
    int animations;
    int movePackets;
    int lastAnimation;
    List<Integer> swingList = new ArrayList<Integer>();

    void check(WrappedInFlyingPacket packet) {
        movePackets++;
        if (animations <= 0) return;
        if (movePackets < 20) return;
        if (System.currentTimeMillis() - lastArmAnimation > 200) return;
        animations = 0;
        movePackets = 0;
    }

    void check(WrappedInArmAnimationPacket packet) {
        if (data.state.isPlacing || data.state.isDigging) {
            animations = 0;
            movePackets = 0;
            swingList.clear();
            return;
        }
        animations++;
        if (lastAnimation > animations) {
            swingList.add(lastAnimation);
            if (swingList.size() == 5) {
                int[] cps = new int[5];
                for (int i = 0; i < 5; ++i) {
                    int swing = swingList.get(i);
                    cps[i] = i == 0 ? swing - 1 : swing;
                }
                int rate = 1;
                for (int i2 = 0; i2 < 4; rate += cps[i2] - cps[i2 + 1], ++i2);
                rate = Math.abs(rate);
                double averageCps = Arrays.stream(cps).average().orElse(0.0);
                if (rate == 1 && averageCps > 8.0 && (double) Math.round(averageCps) == averageCps) {
                    fail(3, 20 * 10);
                }
                swingList.clear();
            }
        }
        lastArmAnimation = System.currentTimeMillis();
        lastAnimation = animations;
    }
}

