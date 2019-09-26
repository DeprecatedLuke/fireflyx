package com.ngxdev.anticheat.checks.combat.autoclicker.experimental;

import com.ngxdev.anticheat.api.check.Check;
import api.CheckWrapper;
import api.CheckType;
import com.ngxdev.anticheat.data.playerdata.PlayerData;
import com.ngxdev.anticheat.utils.PlayerTimer;
import com.ngxdev.tinyprotocol.api.ProtocolVersion;
import com.ngxdev.tinyprotocol.packet.in.WrappedInArmAnimationPacket;
import com.ngxdev.tinyprotocol.packet.in.WrappedInFlyingPacket;
import com.ngxdev.anticheat.utils.Utils;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

import static api.CheckType.Type.AUTOCLICKER;

@CheckType(id = "autoclicker:8", name = "AutoClicker 8", type = AUTOCLICKER, state = CheckType.State.EXPERIMENTAL)
public class AutoClicker8 extends Check {
    int cps, ccps, clicks;
    double distChange, lastDist;
    PlayerTimer lastCheck, lastClick;
    LinkedList<Integer> lastCps = new LinkedList<>();
    List<String> clickHistory = new ArrayList<>();

    @Override
    public void init(PlayerData data, CheckWrapper wrapper) {
        super.init(data, wrapper);
        lastCheck = new PlayerTimer(data);
        lastClick = new PlayerTimer(data);
    }

    void check(WrappedInFlyingPacket packet) {
        if (ccps > 0) {
            if (lastClick.hasPassed(1)) ccps -= 2;
            else if (lastClick.hasPassed(3)) ccps -= 5;
            else if (lastClick.hasPassed(5)) ccps -= 15;
            else ccps--;
        }
    }

    void check(WrappedInArmAnimationPacket packet) {
        if (!canCheck() || data.state.isPlacing || data.state.isDigging || data.protocolVersion.isAbove(ProtocolVersion.V1_8_9)) return;
        if (lastClick.hasPassed(20)) {
            cps = 0;
            lastCheck.reset();
        }
        clicks++;
        ccps += 2;

        // Concurrent Clicks Per Second
        if (ccps > 100) {
            fail(1, 20 * 30);
            ccps = 0;
        } else debug("+%s", ccps);

        if (clicks >= 2) clicks--;

        // Click pattern
        if (lastCheck.hasPassed(20)) {
            lastCps.add(cps);
            if (lastCps.size() >= 5) {
                double avg = lastCps.stream().collect(Collectors.averagingDouble(d -> d));
                clickHistory.add(Utils.format(avg, 2) + "");
                if (clickHistory.size() >= 5) clickHistory.remove(0);
                if (avg > 7) {
                    double dist = 0;
                    for (int cps : lastCps) dist += Math.abs(avg - cps);
                    dist /= lastCps.size() * 0.8;
                    dist *= (20 - avg) * 0.1;
                    distChange = lastDist - dist;
                    lastDist = dist;
                    if (dist < 0.25 && dist > 0) {
                        fail(3, 20 * 30, "^" + Utils.format(dist, 3));
                    }
                }
            }
            if (lastCps.size() > 20) {
                lastCps.remove(0);
            }

            cps = 0;
            lastCheck.reset();
        }
        cps++;
        lastClick.reset();
    }
}