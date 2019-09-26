/*
 * Copyright (c) 2018 NGXDEV.COM. Licensed under MIT.
 */

package com.ngxdev.anticheat.api.check;

import api.CheckType;
import api.CheckWrapper;
import api.ConfigValueX;
import api.FireflyXViolationEvent;
import api.ViolationX;
import com.ngxdev.anticheat.Firefly;
import com.ngxdev.anticheat.api.check.type.Ignore;
import com.ngxdev.anticheat.api.check.type.Parser;
import com.ngxdev.anticheat.data.ViolationData;
import com.ngxdev.anticheat.data.playerdata.PlayerData;
import com.ngxdev.anticheat.handler.TinyProtocolHandler;
import com.ngxdev.anticheat.handler.handler.PlayerSizeHandler;
import com.ngxdev.anticheat.utils.Helper;
import com.ngxdev.anticheat.utils.Materials;
import com.ngxdev.anticheat.utils.TimeTimer;
import com.ngxdev.anticheat.utils.Utils;
import com.ngxdev.anticheat.utils.world.CollisionBox;
import com.ngxdev.anticheat.utils.world.types.SimpleCollisionBox;
import com.ngxdev.tinyprotocol.api.NMSObject;
import com.ngxdev.tinyprotocol.api.ProtocolVersion;
import com.ngxdev.tinyprotocol.packet.in.WrappedInFlyingPacket;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Delegate;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitTask;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import static api.CheckType.Type.AUTOCLICKER;
import static api.CheckType.Type.BADPACKET;
import static api.CheckType.Type.KILLAURA;
import static com.ngxdev.anticheat.Firefly.devServer;
import static com.ngxdev.anticheat.api.check.Priority.Value.NORMAL;

@Getter
public abstract class Check {
	// Static values
	private String id, name, lowerName;
	private CheckType type;
	private CheckType.State state;

	@Setter
	private boolean debug;

	// Configurable values
	public CheckWrapper check;

	public Player player;
	// To make the code cleaner
	@Delegate
	public PlayerData data;

	// Handles how violations work, feel free to modify this
	private ViolationData violationData;

	public Check() {
		if (getClass().isAnnotationPresent(CheckType.class)) {
			type = getClass().getAnnotation(CheckType.class);
			this.id = type.id();
			this.name = type.name();
			this.lowerName = type.name().toLowerCase();
			this.state = type.state();
			this.name = this.name + state.getTag();
		}
	}

	public void init(PlayerData data, CheckWrapper wrapper) {
		this.player = data.player;
		this.data = data;
		this.check = wrapper;
		this.violationData = new ViolationData(data);

		initMethods();
	}

	private void initMethods() {
		for (Method method : getClass().getDeclaredMethods()) {
			if (Arrays.asList("wait").contains(method.getName()) || method.getName().contains("lambda") || method.getParameterCount() != 1)
				continue;
			byte priority = Byte.MAX_VALUE - 10;
			if (getClass().isAnnotationPresent(Parser.class)) priority = NORMAL;
			if (method.isAnnotationPresent(Priority.class)) {
				priority = method.getAnnotation(Priority.class).value();
			}
			if (method.isAnnotationPresent(Ignore.class)) continue;
			method.setAccessible(true);
			data.methods.add(new MethodWrapper(this, method, priority));
			data.initialized = true;
		}
	}

	/**
	 * @param extra - Extra debug data, or values, like reach value
	 * @param args  - Formatter args, "%s there" with arg "hello" will return "hello there"
	 */
	public void debug(Object extra, Object... args) {
		if (debug) {
			player.sendMessage("§e" + name + " §7/ §f" + String.format(extra.toString(), args) + " [" + ThreadLocalRandom.current().nextInt(9) + "]");
		}
	}

	/**
	 * @return if the player should be cancelled, pushed back, etc.
	 */
	public boolean fail() {
		return fail(type.maxVl(), type.timeout(), null);
	}

	/**
	 * @return if the player should be cancelled, pushed back, etc.
	 */
	public boolean fail(String extra, Object... args) {
		return fail(type.maxVl(), type.timeout(), extra, args);
	}

	/**
	 * @return if the player should be cancelled, pushed back, etc.
	 */
	public boolean fail(int violations, int violationTimeouts) {
		return fail(violations, violationTimeouts, null);
	}

	/**
	 * @param violations       - the custom violations for the check to flag
	 * @param violationTimeout - the custom time till violations expire
	 * @param extra            - Extra debug data, or values, like reach value
	 * @param args             - Formatter args, "%s there" with arg "hello" will return "hello there"
	 * @return if the player should be cancelled, pushed back, etc.
	 */
	public boolean fail(int violations, int violationTimeout, String extra, Object... args) {
		//if (true) return false;
		if ((devServer && data.debug.nobypass) || data.state.isIgnored) return false;
		CheckType.Type ctype = this.type.type();
		if (ctype == BADPACKET || ctype == KILLAURA || ctype == AUTOCLICKER) {
			if (data.protocolVersion.isAbove(ProtocolVersion.V1_8_9)) {
				//1.9+ DOES NOT SEND PACKETS EVERY SINGLE TICK WHICH ESSENTIALLY BREAKS ALL OF THE CHECKS WHEN YOU'RE NOT MOVING :(((((((
				if (System.currentTimeMillis() - data.lag.lastPacket > 50) return false;
			}
		}

		int vl = violationData.getViolation(violationTimeout + check.expirationOffset());
		int vls = violations + check.alertOffset();

		// Declared as fields for special occasions that would make it not be required to be alerted (generic lag check for example)
		boolean shouldAlert = (devServer ? type.alert() : check.alert()) && vl >= vls;
		boolean shouldCancel = (check.cancel() || devServer) && vl >= violations + check.cancelOffset();
		boolean shouldBan = check.ban() && vl >= violations + check.banOffset();

		String finalExtra = extra == null ? null : String.format(extra, args);

		if (!devServer) {
			if (shouldAlert && (data.player.hasPermission("firefly.bypass.alert") || data.player.isOp()) && !data.debug.nobypass)
				shouldAlert = false;
			if (shouldCancel && (data.player.hasPermission("firefly.bypass.cancel") || data.player.isOp()) && !data.debug.nobypass)
				shouldCancel = false;
			if (shouldBan && (data.player.hasPermission("firefly.bypass.ban") || data.player.isOp())) shouldBan = false;

			if (shouldAlert) {
				if (!data.lastAlert.computeIfAbsent(this, d -> new TimeTimer(0)).hasPassed(ConfigValueX.ALERT_TIMEOUT.asInteger(), true)) {
					shouldAlert = false;
				}
			}
		}

		if (shouldAlert || shouldBan) {
			Firefly.storage.addAlert(new ViolationX(data.player.getUniqueId(), type.name(), vl, System.currentTimeMillis(), finalExtra + (shouldBan ? " [BAN]" : "")));
		}

		//if (shouldBan && data.isSkipping()) shouldBan = false;
		if (shouldAlert && data.isSkipping() && ctype == CheckType.Type.MOVEMENT) shouldAlert = false;

		boolean finalShouldAlert = shouldAlert;
		boolean finalShouldCancel = shouldCancel;
		boolean finalShouldBan = shouldBan;

		FireflyXViolationEvent event = new FireflyXViolationEvent(data.player, type.name(), finalShouldAlert, finalShouldCancel, finalShouldBan, finalExtra, vl);
		Bukkit.getPluginManager().callEvent(event);
		if (event.isCancelled()) {
			return event.cancel;
		}


		Bukkit.getScheduler().runTask(Firefly.getInstance(), () -> {
			if (data.state.isIgnored) return;
			if (finalShouldBan) {
				data.state.isIgnored = true;

				for (String cmd : ConfigValueX.COMMAND_BAN.asString().split("\\|")) {
					Bukkit.dispatchCommand(Bukkit.getConsoleSender(), replaceValues(cmd, data, Check.this.type, vl, vls, violationData.getTotalViolationCount(), finalExtra));
				}
			}
			if (finalShouldCancel && !finalShouldAlert) {
				if (devServer) {
					player.sendMessage(Firefly.getPrefix() + " §eYou §7were setback: §e" + name + " §8[§7" + vl + "§8/§7" + vls + "§8/§7" + violationData.getTotalViolationCount() + (finalExtra != null ? ("§8/§7" + finalExtra) : "") + "§8]");
					PlayerData.getAll().forEach(data2 -> {
						if (data2.debug.viewall && data2 != data) {
							data2.player.sendMessage(Firefly.getPrefix() + " §e" + player.getName() + " §7was setback: §e" + name + " §8[§7" + vl + "§8/§7" + vls + "§8/§7" + violationData.getTotalViolationCount() + (finalExtra != null ? ("§8/§7" + finalExtra) : "") + "§8]");
						}
					});
				} else {
					PlayerData.getAll().forEach(data2 -> {
						if (data2.debug.debugtp) {
							data2.player.sendMessage(Firefly.getPrefix() + " §e" + player.getName() + " §7was setback: §e" + name + " §8[§7" + vl + "§8/§7" + vls + "§8/§7" + violationData.getTotalViolationCount() + (finalExtra != null ? ("§8/§7" + finalExtra) : "") + "§8]");
						}
					});
				}
			}
			if (finalShouldAlert) {
				if (devServer) {
					player.sendMessage(Firefly.getPrefix() + " §eYou §7have failed: §e" + name + " §8[§7" + vl + "§8/§7" + vls + "§8/§7" + violationData.getTotalViolationCount() + (finalExtra != null ? ("§8/§7" + finalExtra) : "") + "§8]");
					PlayerData.getAll().forEach(data2 -> {
						if (data2.debug.viewall && data2 != data) {
							data2.player.sendMessage(Firefly.getPrefix() + " §e" + player.getName() + " §7has failed: §e" + name + " §8[§7" + vl + "§8/§7" + vls + "§8/§7" + violationData.getTotalViolationCount() + (finalExtra != null ? ("§8/§7" + finalExtra) : "") + "§8]");
						}
					});
				} else {
					for (String cmd : ConfigValueX.COMMAND_ALERT.asString().split("\\|")) {
						Bukkit.dispatchCommand(Bukkit.getConsoleSender(), replaceValues(cmd, data, Check.this.type, vl, vls, violationData.getTotalViolationCount(), finalExtra));
					}
				}
			}
		});

		return finalShouldCancel;
	}

	private static String replaceValues(String cmd, PlayerData data, CheckType module, int vls, int max, int total, String debug) {
		return Utils.convert(cmd
				.replace("{name}", ConfigValueX.ANTICHEAT_NAME.asString())
				.replace("{module}", module.name())
				.replace("{check}", module.name())
				.replace("{vl}", ConfigValueX.ALERT_VIOLATIONS.asString().replace("{vl}", Integer.toString(vls)))
				.replace("{max}", Integer.toString(max))
				.replace("{total}", Integer.toString(total))
				.replace("{debug}", debug != null ? ConfigValueX.ALERT_DEBUG.asString().replace("{debug}", debug) : "")
				.replace("{player}", data.player.getName()));
	}


	public void decrease() {
		violationData.removeFirst();
	}

	public void decrease(int count) {
		for (int i = 0; i < count; i++) violationData.removeFirst();
	}

	/**
	 * Teleport the player back to it's previous position.
	 */
	public void setback() {
		if (canNotSetback()) return;
		data.state.isSettingback = true;
		runSync(() -> player.teleport(new Location(player.getWorld(), data.movement.fx, data.movement.fy, data.movement.fz, player.getLocation().getYaw(), player.getLocation().getPitch())));
	}

	/**
	 * Teleport the player back to it's previous position.
	 */
	public void setback(boolean fucked) {
		if (canNotSetback()) return;
		data.state.isSettingback = true;
		runSync(() -> player.teleport(new Location(player.getWorld(), data.movement.fx, data.movement.fy, data.movement.fz, Utils.clamp180(ThreadLocalRandom.current().nextFloat() * 180), (ThreadLocalRandom.current().nextFloat() * 180) - 90)));
	}


	/**
	 * Teleport the player back to it's previous position.
	 */
	public void setback(double downshift) {
		if (canNotSetback()) return;
		data.state.isSettingback = true;
		if (downshift != 0 || data.enviorment.onGround.wasNotReset()) {
			SimpleCollisionBox box = Helper.getMovementHitbox(player);
			box.yMin -= downshift + 1;
			List<Block> blocks = Helper.getBlocksNearby2(data.player.getWorld(), box, Materials.SOLID);
			List<CollisionBox> boxes_ = Helper.toCollisions(blocks);
			List<SimpleCollisionBox> boxes = Helper.collisionsDowncasted(boxes_, box);
			downshift = box.yMin;
			for (SimpleCollisionBox b : boxes)
				if (b.yMax > downshift) downshift = b.yMax;
			double finalDownshift = downshift;
			runSync(() -> player.teleport(new Location(player.getWorld(), data.movement.fx, finalDownshift, data.movement.fz, player.getLocation().getYaw(), player.getLocation().getPitch())));
		} else runSync(this::setback);
	}

	private boolean canNotSetback() {
		return data.state.isSettingback || player.isDead() || data.state.isTeleporing || data.movement.fy == 0;
	}

	/**
	 * Helper Method
	 */
	public void sendPacket(NMSObject packet) {
		try {
			sendPacket(packet.getObject());
		} catch (Throwable t) {

		}
	}

	/**
	 * Helper Method
	 */
	public void sendPacket(Object packet) {
		TinyProtocolHandler.sendPacket(player, packet);
	}

	/**
	 * A generic check to see if a player can even be checked for cheats.
	 */
	public boolean canCheck() {
		return (data.gamemode == GameMode.SURVIVAL || data.gamemode == GameMode.ADVENTURE);
	}

	/**
	 * A generic check to see if a player can even be checked for movement cheats.
	 */
	public boolean canCheckMovement() {
		return canCheck() && !data.state.isTeleporing && !data.state.isSettingback && !canFly() && player.getVehicle() == null && data.timers.join.hasPassed(10) && data.enviorment.handler != null;
	}

	public boolean isMoving() {
		return data.movement.deltaH != 0 || data.movement.deltaV != 0;
	}

	/**
	 * Checks is a player is currently gliding.
	 */
	public boolean isGliding() {
		return PlayerSizeHandler.getInstance().isGliding(player);
	}

	/**
	 * Checks if a player has the ability to fly.
	 */
	public boolean canFly() {
		return player.getAllowFlight() || player.getVehicle() != null;
	}

	/**
	 * The most accurate way to check if a player is on the ground.
	 */
	public boolean isAccurateOnGround(WrappedInFlyingPacket packet) {
		return data.enviorment.onGround.getResetStreak() <= 3/*was just reset, not trusting the server!*/ ? packet.isGround() || data.enviorment.onGround.wasReset() : data.enviorment.onGround.wasReset();
	}

	public void runSync(Runnable o) {
		if (Bukkit.isPrimaryThread()) o.run();
		else Bukkit.getScheduler().runTask(Firefly.getInstance(), o);
	}

	public void runSync(Runnable o, int delay) {
		Bukkit.getScheduler().runTaskLater(Firefly.getInstance(), o, delay);
	}

	public int getPacketOffset() {
		return Math.abs(50 - data.lag.packetDelay);
	}

	public double getBlockFriction() {
		return data.enviorment.onIce.wasReset() ? 0.98 : 0.60;
	}

	///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	public List<BukkitTask> tasks = new ArrayList<>();

	public void schedule(Runnable run) {
		tasks.add(Bukkit.getScheduler().runTask(Firefly.getInstance(), run));
	}

	public void schedule(Runnable run, long delay) {
		tasks.add(Bukkit.getScheduler().runTaskLater(Firefly.getInstance(), run, delay));
	}

	public void schedule(Runnable run, long delay, long interval) {
		tasks.add(Bukkit.getScheduler().runTaskTimer(Firefly.getInstance(), run, delay, interval));
	}

	///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	public int getPotionEffectLevel(PotionEffectType type) {
		if (player.hasPotionEffect(type)) {
			for (PotionEffect effect : player.getActivePotionEffects()) {
				if (effect.getType().equals(type)) {
					return effect.getAmplifier() + 1;
				}
			}
		}

		return 0;
	}
}
