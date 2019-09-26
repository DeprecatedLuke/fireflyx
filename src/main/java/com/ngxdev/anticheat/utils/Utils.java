package com.ngxdev.anticheat.utils;

import net.md_5.bungee.api.ChatColor;
import org.apache.commons.lang.text.StrBuilder;
import org.bukkit.Location;
import org.bukkit.util.Vector;

import java.io.Closeable;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.text.DecimalFormat;
import java.util.*;
import java.util.stream.Stream;

public class Utils {
	public static ClassLoader injectorClassLoader = Utils.class.getClassLoader();
	public static final double SIXTEENTH = 0.0625;
	public static final double EIGHTH = 0.125;
	public static final double FOURTH = EIGHTH * 2;
	public static final double HALF = .5;

	public static UUID toUUID(String uuid) {
		if (uuid.contains("-"))
			return UUID.fromString(uuid);
		return UUID.fromString(uuid.replaceFirst("([0-9a-fA-F]{8})([0-9a-fA-F]{4})([0-9a-fA-F]{4})([0-9a-fA-F]{4})([0-9a-fA-F]+)", "$1-$2-$3-$4-$5"));
	}

	public static <K, V extends Comparable<? super V>> Map<K, V> sortByValue(Map<K, V> map) {
		List<Map.Entry<K, V>> list = new ArrayList<>(map.entrySet());
		list.sort(Map.Entry.comparingByValue());

		Map<K, V> result = new LinkedHashMap<>();
		for (Map.Entry<K, V> entry : list) {
			result.put(entry.getKey(), entry.getValue());
		}

		return result;
	}

	public static String drawUsage(long max, long time) {
		double chunk = max / 50;
		StringBuilder line = new StringBuilder("[");
		for (int i = 0; i < 50; i++) {
			line.append((chunk * i < time ? "§c" : "§7") + "❘");
		}
		String zeros = "00";
		String nums = Integer.toString((int) ((time / (double) max) * 100));
		return line.toString() + "§f] §c" + zeros.substring(0, 3 - nums.length()) + nums + "% §f❘";
	}

	public static String join(Stream<String> list, String separator) {
		StringBuilder builder = new StringBuilder();
		list.forEach(e -> {
			if (builder.length() != 0) builder.append(separator);
			builder.append(e);
		});
		return builder.toString();
	}

	public static double getAngle(Location loc1, Location loc2) {
		if (loc1 == null || loc2 == null) return -1;
		Vector playerRotation = new Vector(loc1.getYaw(), loc1.getPitch(), 0.0f);
		loc1.setY(0);
		loc2.setY(0);
		Vector expectedRotation = getRotation(loc1, loc2);
		return clamp180(playerRotation.getX() - expectedRotation.getX());
	}

	public static Vector getRotation(Location loc1, Location loc2) {
		double dx = loc2.getX() - loc1.getX();
		double dy = loc2.getY() - loc1.getY();
		double dz = loc2.getZ() - loc1.getZ();
		double distanceXZ = Math.sqrt(dx * dx + dz * dz);
		float yaw = (float) (Math.atan2(dz, dx) * 180.0 / 3.141592653589793) - 90.0f;
		float pitch = (float) (-(Math.atan2(dy, distanceXZ) * 180.0 / 3.141592653589793));
		return new Vector(yaw, pitch, 0.0f);
	}

	public static double getHorizontalDistance(Location one, Location two) {
		double toReturn;
		double xSqr = (two.getX() - one.getX()) * (two.getX() - one.getX());
		double zSqr = (two.getZ() - one.getZ()) * (two.getZ() - one.getZ());
		double sqrt = Math.sqrt(xSqr + zSqr);
		toReturn = Math.abs(sqrt);
		return toReturn;
	}

	public static double clamp180(double theta) {
		theta %= 360.0;
		if (theta >= 180.0) theta -= 360.0;
		if (theta < -180.0) theta += 360.0;
		return theta;
	}

	public static float clamp180(float theta) {
		theta %= 360.0;
		if (theta >= 180.0) theta -= 360.0;
		if (theta < -180.0) theta += 360.0;
		return theta;
	}

	private static final int[] decimalPlaces = {0, 10, 100, 1000, 10000, 100000, 1000000, 10000000, 100000000, 1000000000};

	public static double format(double d, int dec) {
		return (long) (d * decimalPlaces[dec] + 0.5) / (double) decimalPlaces[dec];
	}

	public static float sqrt(float number) {
		return (float) Math.sqrt((double) number);
	}

	public static boolean contains(Object[] array, Object obj) {
		for (Object object : array) if (object != null && object.equals(obj)) return true;
		return false;
	}

	public static String concat(String[] arguments) {
		StringJoiner joiner = new StringJoiner(" ");
		for (String s : arguments) joiner.add(s);
		return joiner.toString();
	}

	public static void download(File file, String from) throws Exception {
		URL url = new URL(from);
		InputStream stream = url.openStream();
		ReadableByteChannel channel = Channels.newChannel(stream);
		FileOutputStream out = new FileOutputStream(file);
		out.getChannel().transferFrom(channel, 0L, Long.MAX_VALUE);
	}

	public static void injectURL(URL url) {
		try {
			URLClassLoader systemClassLoader = (URLClassLoader) injectorClassLoader;
			Class<URLClassLoader> classLoaderClass = URLClassLoader.class;

			try {
				Method method = classLoaderClass.getDeclaredMethod("addURL", URL.class);
				method.setAccessible(true);
				method.invoke(systemClassLoader, url);
			} catch (Throwable t) {
				t.printStackTrace();
			}
		} catch (Exception e) {
		}
	}

	public static void close(Closeable... closeables) {
		try {
			for (Closeable closeable : closeables) if (closeable != null) closeable.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void close(AutoCloseable... closeables) {
		try {
			for (AutoCloseable closeable : closeables) if (closeable != null) closeable.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void sleep(long time) {
		try {
			Thread.sleep(time);
		} catch (Throwable throwable) {

		}
	}

	public static String convert(String message) {
		return ChatColor.translateAlternateColorCodes('&', message);
	}

	public static String strip(String message) {
		return ChatColor.stripColor(message);
	}

	public static String capitalize(String str) {
		int strLen;
		return str != null && (strLen = str.length()) != 0 ? (new StrBuilder(strLen)).append(Character.toTitleCase(str.charAt(0))).append(str.substring(1)).toString() : str;
	}

	public static final DecimalFormat df = new DecimalFormat("#.#");

	public static String formatMillis(Long milis) {
		double seconds = (double) Math.max(0, milis) / 1000;
		double minutes = seconds / 60;
		double hours = minutes / 60;
		double days = hours / 24;
		double weeks = days / 7;
		double months = days / 31;
		double years = months / 12;


		if (years >= 1) {
			return df.format(years) + " year" + (years != 1 ? "s" : "");
		} else if (months >= 1) {
			return df.format(months) + " month" + (months != 1 ? "s" : "");
		} else if (weeks >= 1) {
			return df.format(weeks) + " week" + (weeks != 1 ? "s" : "");
		} else if (days >= 1) {
			return df.format(days) + " day" + (days != 1 ? "s" : "");
		} else if (hours >= 1) {
			return df.format(hours) + " hour" + (hours != 1 ? "s" : "");
		} else if (minutes >= 1) {
			return df.format(minutes) + " minute" + (minutes != 1 ? "s" : "");
		} else {
			return df.format(seconds) + " second" + (seconds != 1 ? "s" : "");
		}
	}
}
