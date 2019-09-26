/*
 * Copyright (c) 2018 NGXDEV.COM. Licensed under MIT.
 */

package com.ngxdev.anticheat.data;

import com.ngxdev.anticheat.data.playerdata.PlayerData;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import java.util.LinkedList;

@RequiredArgsConstructor
public class ViolationData {
    @NonNull public final PlayerData data;
    @Getter private LinkedList<Integer> violations = new LinkedList<>();
    @Getter private long lastTime;

    @Getter private int totalViolationCount = 0;

    public int getViolation(long time) {
        violations.add(data.currentTick);
        totalViolationCount++;
        if (time != -1) violations.removeIf(l -> data.currentTick - l > time);
        lastTime = time;
        return violations.size();
    }

    public int getViolationCount() {
        if (lastTime != 0) violations.removeIf(l -> data.currentTick - l > lastTime);
        return violations.size();
    }

    public void removeFirst() {
        if (!violations.isEmpty()) violations.removeFirst();
    }

    public void clearViolations() {
        violations.clear();
    }
}
