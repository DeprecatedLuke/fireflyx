/*
 * Copyright (c) 2018 NGXDEV.COM. Licensed under MIT.
 */

package com.ngxdev.anticheat.utils;

public class TimeTimer {
    long startTime;

    public TimeTimer(long time) {
        this.startTime = time;
    }

    public TimeTimer() {
        this.reset();
    }

    public long time() {
        return startTime;
    }

    public void reset() {
        this.startTime = System.currentTimeMillis();
    }

    public long getPassed() {
        return System.currentTimeMillis() - this.startTime;
    }

    public void add(int amount) {
        this.startTime -= amount;
    }

    public boolean hasPassed(long toPass) {
        return this.getPassed() >= toPass;
    }

    public boolean hasPassed(long toPass, boolean reset) {
        boolean passed = this.getPassed() >= toPass;
        if (passed && reset) reset();
        return passed;
    }
}
