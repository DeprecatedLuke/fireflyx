/*
 * Copyright (c) 2018 NGXDEV.COM. Licensed under MIT.
 */

package com.ngxdev.anticheat.data.playerdata;

import com.avaje.ebean.validation.NotNull;
import com.ngxdev.anticheat.Firefly;
import com.ngxdev.anticheat.api.HumanNPC;
import com.ngxdev.anticheat.api.check.Check;
import com.ngxdev.anticheat.api.check.MethodWrapper;
import com.ngxdev.anticheat.data.TimedLocation;
import com.ngxdev.anticheat.handler.CheckHandler;
import com.ngxdev.anticheat.handler.TinyProtocolHandler;
import com.ngxdev.anticheat.utils.Helper;
import com.ngxdev.anticheat.utils.Pair;
import com.ngxdev.anticheat.utils.PlayerTimer;
import com.ngxdev.anticheat.utils.TimeTimer;
import com.ngxdev.anticheat.utils.evicting.EvictingList;
import com.ngxdev.anticheat.utils.exception.ExceptionLog;
import com.ngxdev.anticheat.utils.world.CollisionHandler;
import com.ngxdev.tinyprotocol.api.ProtocolVersion;
import com.ngxdev.tinyprotocol.reflection.FieldAccessor;
import com.ngxdev.tinyprotocol.reflection.MethodInvoker;
import com.ngxdev.tinyprotocol.reflection.Reflection;
import lombok.NonNull;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.Vector;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Stream;

// Note, the only reason these don't have getters and setters to make the code cleaner in checks
public class PlayerData {
	private static final MethodInvoker getPlayerHandle = Reflection.getMethod("{obc}.entity.CraftPlayer", "getHandle");
	private static final FieldAccessor<Object> getConnection = Reflection.getField("{nms}.EntityPlayer", "playerConnection", Object.class);

	@NonNull
	public final Player player;
	public final Collection<Player> singleton;

	public final List<Check> allChecks = new ArrayList<>();
	public final List<Check> validChecks = new ArrayList<>();
	public final LinkedList<MethodWrapper> methods = new LinkedList<>();
	public final Map<Check, TimeTimer> lastAlert = new HashMap<>();
	public boolean initialized = false;
	public Object playerConnection;
	public int movementTicks = 0;

	public PlayerData(Player player) {
		this.player = player;
		this.singleton = Collections.singleton(player);
		this.gamemode = player.getGameMode();
		try {
			this.protocolVersion = ProtocolVersion.getVersion(TinyProtocolHandler.getProtocolVersion(player));
		} catch (Exception e) {
			this.protocolVersion = ProtocolVersion.V1_8_9;
		}
		playerConnection = getConnection.get(getPlayerHandle.invoke(player));
	}

	// Global tags
	public GameMode gamemode;
	public int currentTick = 0;
	public ProtocolVersion protocolVersion;
	public final EvictingList<Pair<TimedLocation, Double>> locations = new EvictingList<>(20);

	public class NPC {
		private int uid;
		public HumanNPC npc = new HumanNPC(player.getUniqueId(), "§g§l§h§f§d§i§c§k" + uid++);
		public List<HumanNPC> npcs = new ArrayList<>();
	}

	public class Velocity {
		public double deltaH, deltaV;
	}

	public class Movement {
		public double fx, fy, fz; // Last location
		public double tx, ty, tz; // Current location
		public float fyaw, fpitch, tyaw, tpitch;
		public double deltaH;
		public double deltaV;

		public double lastDeltaH;
		public double lastDeltaV;

		public boolean hasJumped, inAir;

		public float deltaYaw, deltaPitch;
		public float lastDeltaYaw, lastDeltaPitch;
		public float yawDifference, pitchDifference;


		public Vector tPos() {
			return new Vector(tx, ty, tz);
		}

		public Vector fPos() {
			return new Vector(fx, fy, fz);
		}

		public Vector tEyePos() {
			return new Vector(tx, ty + player.getEyeHeight(), tz);
		}

		public Vector fEyePos() {
			return new Vector(fx, fy + player.getEyeHeight(), fz);
		}

		public Vector tDir() {
			return Helper.vector(tyaw, tpitch);
		}

		public Vector fDir() {
			return Helper.vector(fyaw, fpitch);
		}

	}

	public class Enviorment {
		public PlayerTimer
				onGround, onGroundClient, inAir,
				onIce, onLadder, onSoulSand, onSlime,
				inWater, inLava, inWeb, inLiquid,
				blockAbove, onWeirdBlock,
				collidedHorizontally, collidedVertically;

		public CollisionHandler lastHandler, handler;
	}

	public class Timers {
		public PlayerTimer lastJump;
		public PlayerTimer lastTeleport;
		public PlayerTimer inUnloadedChunks;
		public PlayerTimer lastFlightToggle;
		public PlayerTimer horizontalIdle;
		public PlayerTimer lastAttack;
		public PlayerTimer lastSprint;
		public PlayerTimer lastArmAnimation;
		public PlayerTimer lastSprintCancel;
		public PlayerTimer lastVelocity;
		public PlayerTimer lastVelocitySent;
		public PlayerTimer slimePush;
		public PlayerTimer join;
		public PlayerTimer lastSneak;
		public PlayerTimer lastBlockGlitch;
		public PlayerTimer lastWankVelocity;
		public TimeTimer lastPacket = new TimeTimer();
	}

	public class State {
		public boolean isSettingback,
				isTeleporing,
				isTakingVelocity,
				isPlacing,
				isDigging,
				isInventoryOpen,
				isUsingBow,
				isIgnored;

		public int cancelHits = 0;
	}

	public class Lag {
		public int keepAlivePing,
				transactionPing;
		public long currentTime;

		public int packetDelay;
		public long lastPacket = System.currentTimeMillis();
		public int differencial;
		public int skips = 10;
		public long playerTime = -1L;
	}

	public boolean isSkipping() {
		int skips = lag.skips == 0 ?
				(System.currentTimeMillis() - lag.lastPacket) > 75 ? 1 : 0
				: lag.skips;
		return skips > 0;
	}

	public class AutoClicker {
		public int cps;
	}

	public class Debug {
		public boolean viewall = true;
		public boolean nobypass;
		public boolean debugtp;
	}

	// Helper methods
	public List<TimedLocation> getEstimatedLocations(long time, int offset) {
		List<TimedLocation> possible = new ArrayList<>();
		//player.sendMessage("--------------------");
		for (int i = locations.size() - 1; i > 0; i--) {
			TimedLocation location = locations.get(i).getX();
			long diff = time - location.getTime(); // 100, 150
			//player.sendMessage(diff + "|" + offset);
			if (Math.abs(diff) < offset) possible.add(location);
		}
		return possible;
	}

	public int getEstimatedPacketLag() {
		for (int i = locations.size() - 1; i > 0; i--) {
			TimedLocation location = locations.get(i).getX();
			long diff = lag.currentTime - location.getTime(); // 100, 150
			if (diff > 0) return i;
		}
		return 10;
	}

	public Map<String, BukkitTask> scheduledTasks = new HashMap<>();

	public void schedule(String id, Runnable run, long time, long repeat) {
		scheduledTasks.put(id, Bukkit.getScheduler().runTaskTimer(Firefly.getInstance(), run, time, repeat));
	}

	public void stop(String id) {
		scheduledTasks.remove(id).cancel();
	}


	// Ugly I know, these get automatically initialized
	public Velocity velocity;
	public Movement movement;
	public Enviorment enviorment;
	public Timers timers;
	public State state;
	public Lag lag;
	public AutoClicker autoclicker;
	public Debug debug;
	public NPC npc;

	public <T> T find(Class<? extends T> clazz) {
		return (T) allChecks.stream().filter(c -> c.getClass() == clazz).findFirst().orElse(null);
	}

	public static Stream<PlayerData> getAll() {
		return Bukkit.getOnlinePlayers().stream().map(PlayerData::get);
	}

	public static PlayerData get(Integer id) {
		Player player = Bukkit.getOnlinePlayers().stream().filter((p) -> p.getEntityId() == id).findFirst().orElse(null);
		if (player == null || !player.isOnline()) return null;
		return PlayerDataManager.get(player);
	}

	public static @NotNull
	PlayerData get(Player player) { // lol, suppress nullexception, even though it returns null~
		PlayerData data = PlayerDataManager.get(player);
		if (data == null) {
			data = new PlayerData(player);
			// We auto-initialize the fields to make PlayerData cleaner.
			for (Field f : data.getClass().getFields()) {
				try {
					if (!f.getType().isPrimitive() && f.get(data) == null) {
						if (isDataClass(f.getType())) {
							Object inst = f.getType().getConstructor(PlayerData.class).newInstance(data);
							f.set(data, inst);
							for (Field field : f.getType().getFields()) {
								if (field.getType() == PlayerTimer.class) {
									field.set(inst, new PlayerTimer(data));
								}
							}
						}
					}
				} catch (Exception ex) {
					ex.printStackTrace();
				}
			}
			CheckHandler.init(data);
			PlayerDataManager.set(player, data);
		}
		return data;
	}

	private static boolean isDataClass(Class clazz) {
		for (Constructor cons : clazz.getDeclaredConstructors()) {
			if (cons.getParameterCount() == 1 && cons.getParameterTypes()[0] == PlayerData.class) return true;
		}
		return false;
	}

	public static PlayerData getOrNull(Player player) { // lol, supress nullexception, even though it returns null~
		return PlayerDataManager.get(player);
	}

	public void fireChecks(Object argument) {
		//services[assignedCore % available].submit(() -> {
		//Bukkit.getScheduler().runTask(Firefly.getInstance(), () -> {
			for (MethodWrapper m : methods) {
				try {
					long start = System.currentTimeMillis();
					m.call(argument);
					long elapsed = System.currentTimeMillis() - start;
					if (elapsed > 50) {
						System.out.println("[WARN] Took " + elapsed + "ms to execute argument " + argument.getClass().getSimpleName() + " for: " + m.getCheck().getId() + " for method " + m.getMethod().getParameterTypes()[0].getSimpleName());
					}
				} catch (Exception e) {
					//System.out.println("Failed to call " + m.getMethod().getName());
					ExceptionLog.log(e);
				}
			}
		//});
		//});
	}
}
