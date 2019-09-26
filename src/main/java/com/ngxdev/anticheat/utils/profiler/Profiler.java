/*
 * Copyright (c) 2018 NGXDEV.COM. Licensed under MIT.
 */

package com.ngxdev.anticheat.utils.profiler;

public interface Profiler {
    void start(String name);

    void start();

    void stop(String name, long extense);

    void stop(String name);

    void stopCustom(String name, long time);

    void stop();
}
