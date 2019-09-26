/*
 * Copyright (c) 2018 NGXDEV.COM. Licensed under MIT.
 */

package com.ngxdev.anticheat.utils.exception;

import com.ngxdev.anticheat.utils.TimeTimer;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static com.ngxdev.anticheat.utils.Log.println;

public class ExceptionLog {
    public static long logTimeout = TimeUnit.MINUTES.toMillis(1);
    private static Map<String, TimeTimer> logged = new HashMap<>();

    public static void log(Throwable t) {
        String message = t.getStackTrace().length == 0 ? t.getMessage() : (t.getMessage() + t.getStackTrace()[0].toString());
        TimeTimer lastLog = logged.get(message);
        if (lastLog == null) {
            long id = System.currentTimeMillis();
            logged.put(message, new TimeTimer(id));
            println("[Exception Registry] Exception logged for the first time:");
            t.printStackTrace();
        } else {
            if (lastLog.hasPassed(logTimeout, true)) {
                println("[Exception Registry] Exception logged again:");
                t.printStackTrace();
            }
        }
    }
}
