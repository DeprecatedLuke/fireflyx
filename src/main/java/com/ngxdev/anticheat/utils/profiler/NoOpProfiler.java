/*
 * Copyright (c) 2018 NGXDEV.COM. Licensed under MIT.
 */

package com.ngxdev.anticheat.utils.profiler;

public class NoOpProfiler implements Profiler {
    public static final Profiler NOOP = new NoOpProfiler();

    @Override
    public void start() {

    }

    @Override
    public void start(String name) {

    }

    @Override
    public void stop() {

    }

    @Override
    public void stop(String name) {

    }

    @Override
    public void stop(String name, long extense) {

    }

    @Override
    public void stopCustom(String name, long time) {

    }
}
