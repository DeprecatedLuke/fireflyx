/*
 * Copyright (c) 2018 NGXDEV.COM. Licensed under MIT.
 */

package com.ngxdev.anticheat.utils.profiler;

import java.util.HashMap;
import java.util.Map;

public class InstantProfiler implements Profiler {
	public Map<String, Long> timings = new HashMap<>();
	public Map<String, Long> stddev = new HashMap<>();
	public Map<String, Long> total = new HashMap<>();
	public Map<String, Long> samples = new HashMap<>();
	public long lastSample = 0;

	@Override
	public void start() {
		StackTraceElement stack = Thread.currentThread().getStackTrace()[2];
		start(stack.getMethodName());
	}

	@Override
	public void start(String name) {
		timings.put(name, System.nanoTime());
	}

	@Override
	public void stop() {
		long extense = System.nanoTime();
		StackTraceElement stack = Thread.currentThread().getStackTrace()[2];
		stop(stack.getMethodName(), extense);
	}

	@Override
	public void stop(String name) {
		long extense = System.nanoTime();
		long start = timings.get(name);
		long time = Math.max(0, (System.nanoTime() - start) - (System.nanoTime() - extense));
		long lastTotal = total.getOrDefault(name, time);
		long sample = samples.getOrDefault(name, time);

		samples.put(name, time);
		stddev.put(name, Math.abs(sample - time));

		total.put(name, lastTotal + time);
		lastSample = System.currentTimeMillis();
	}

	public void stopCustom(String name, long time) {
		long lastTotal = total.getOrDefault(name, time);
		long sample = samples.getOrDefault(name, time);

		samples.put(name, time);
		stddev.put(name, Math.abs(sample - time));

		total.put(name, lastTotal + time);
		lastSample = System.currentTimeMillis();
	}

	@Override
	public void stop(String name, long extense) {
		long start = timings.get(name);
		long time = (System.nanoTime() - start) - (System.nanoTime() - extense);
		long lastTotal = total.getOrDefault(name, time);
		long sample = samples.getOrDefault(name, time);

		samples.put(name, time);
		stddev.put(name, Math.abs(sample - time));

		total.put(name, lastTotal + time);
		lastSample = System.currentTimeMillis();
	}
}


class TestProfiler {
	static InstantProfiler profiler = new InstantProfiler();

	public static void main(String... args) {
		profiler.start("start");
		profiler.stop("start");
		for (int i = 0; i < 100; i++) {
			profileMe();
		}
	}

	private static void profileMe() {
		profiler.start("start");
		try {
			Object a = 0;
			int b = (int) a;
			int b1 = (int) a;
			int b2 = (int) a;
			int b3 = (int) a;
			int b4 = (int) a;
			int b5 = (int) a;
			int b6 = (int) a;
			a = b;
			a = b1;
			a = b2;
			a = b3;
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println("Last Sample: " + (profiler.total.get("start") / 1000000D));
		System.out.println("STD: " + (profiler.stddev.get("start") / 1000000D));
		profiler.stop("start");
	}
}
