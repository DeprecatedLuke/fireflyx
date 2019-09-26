package com.ngxdev.anticheat.checks.movement.speed;

import api.CheckType;
import api.CheckWrapper;
import com.ngxdev.anticheat.api.check.Check;
import com.ngxdev.anticheat.checks.movement.experimental.NoSlow;
import com.ngxdev.anticheat.data.TimedLocation;
import com.ngxdev.anticheat.data.playerdata.PlayerData;
import com.ngxdev.anticheat.utils.Pair;
import com.ngxdev.tinyprotocol.packet.in.WrappedInFlyingPacket;
import com.ngxdev.anticheat.utils.Materials;
import com.ngxdev.anticheat.utils.Utils;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;
import java.util.List;

import static api.CheckType.Type.MOVEMENT;

@CheckType(id = "speed:h", name = "Speed H", type = MOVEMENT)
public class SpeedH extends Check {
	public double previousDistance;
	private double drag = 0.91;
	private int fallTicks;
	private int noSlowStreak;
	private Enchantment DEPTH_STRIDER;
	private double omniVl = 0;

	@Override
	public void init(PlayerData data, CheckWrapper wrapper) {
		super.init(data, wrapper);
		try {
			DEPTH_STRIDER = Enchantment.DEPTH_STRIDER;
		} catch (Throwable t) {

		}
	}

	void check(WrappedInFlyingPacket packet) {
		if (!packet.isPos()
				|| !isMoving()
				|| !canCheckMovement()
				|| isGliding()) return;
		List<String> tags = new ArrayList<>();
		double deltaY = data.movement.ty - data.movement.fy;

		double moveSpeed = Math.pow(player.getWalkSpeed() * 5, 2);
		double drag = this.drag;
		boolean onGround = packet.isGround() || data.enviorment.onSlime.wasReset();

		if (deltaY < 0) fallTicks++;
		else fallTicks = 0;

		Material type = player.getWorld().getBlockAt(player.getLocation().getBlockX(), (int) (player.getLocation().getY() - 1.8), player.getLocation().getBlockZ()).getType();

		if (onGround || data.movement.hasJumped) {
			tags.add("ground");
			drag *= 0.91;
			moveSpeed *= drag > 0.708 ? 1.3 : 0.23315;
			moveSpeed *= 0.16277136 / Math.pow(drag, 3);

			if (deltaY > 0) {
				tags.add("ascend");
				moveSpeed += 0.2;

				if (data.movement.hasJumped) {
					tags.add("hop");
					moveSpeed += 0.05;
					if (data.enviorment.onSlime.hasNotPassed(10)) {
						tags.add("slimehop");
						moveSpeed += 0.1;
					}
				}
			} else if (deltaY < 0.0) {
				tags.add("fall");
				moveSpeed -= 0.1;
				if (data.enviorment.onSlime.hasNotPassed(10)) {
					tags.add("slimefall");
					moveSpeed += 0.1;
				}
			} else {
				if (data.timers.lastTeleport.hasNotPassed(5)) {
					moveSpeed *= 2;
				}

				tags.add("hover");
				moveSpeed += 0.05;
				if (data.timers.lastAttack.hasNotPassed(10)) moveSpeed += 0.2;
			}
		} else {
			tags.add("air");
			moveSpeed = 0.056;
			drag = 0.91;

			if (data.timers.lastTeleport.hasNotPassed(5)) {
				moveSpeed *= 1.5;
				tags.add("tp");
			}

			if (fallTicks == 1 && data.enviorment.inLava.wasNotReset()) {
				double dy = Math.abs(deltaY);
				if (dy > 0.08 || dy < 0.07) {
					tags.add("fallen");
					moveSpeed /= (dy * 150);
				}
			}

			if (data.enviorment.onSoulSand.hasNotPassed(20)) {
				moveSpeed += 0.1;
				if (type == Material.ICE || type == Material.PACKED_ICE) {
					moveSpeed += 0.1;
					tags.add("souliceair");
				} else tags.add("soulair");
			}
			if (data.enviorment.onSlime.hasNotPassed(10)) {
				tags.add("slimeair");
				moveSpeed += 0.2;
			}
		}

		data.enviorment.handler.setOffset(1);
		data.enviorment.handler.setSize(0.6, 1);

		if (data.enviorment.inWater.wasReset()
				&& data.enviorment.handler.isCollidedWith(Materials.WATER)) {
			tags.add("water");
			moveSpeed *= 0.8;
		}

		if (data.enviorment.inLava.wasReset()
				&& player.getNoDamageTicks() == player.getMaximumNoDamageTicks()
				&& data.enviorment.handler.isCollidedWith(Materials.LAVA)) {
			tags.add("lava");
			moveSpeed *= 0.6;
		}

		data.enviorment.handler.setOffset(0);

		double previousHorizontal = previousDistance;
		double horizontalDistance = data.movement.deltaH;
		boolean underBlock = data.enviorment.blockAbove.hasNotPassed(5);

		if (underBlock) {
			tags.add("under");
			moveSpeed += 2.6;
		}

		if (data.movement.deltaV > 0.01 && data.movement.deltaV < 0.02) {
			tags.add("waterjump");
			moveSpeed += 1;
		}

		if (data.player.getInventory().getBoots() != null && DEPTH_STRIDER != null) {
			int lvl = data.player.getInventory().getBoots().getEnchantmentLevel(DEPTH_STRIDER);
			if (lvl != 0) {
				tags.add("depthstrider");
				moveSpeed += lvl;
			}
		}

		if (data.enviorment.onWeirdBlock.wasReset()) {
			tags.add("weird");
			moveSpeed += 0.2;
		}

		if (data.timers.lastTeleport.hasNotPassed(3)) moveSpeed += 1;

		moveSpeed += data.velocity.deltaH * (data.timers.lastWankVelocity.hasNotPassed(10) ? 2 : 1);

		int speed = getPotionEffectLevel(PotionEffectType.SPEED);

		if (player.hasPotionEffect(PotionEffectType.SPEED)) {
			tags.add("speed");
			moveSpeed += (speed * .06);
		}

		int jump = getPotionEffectLevel(PotionEffectType.JUMP);

		if (player.hasPotionEffect(PotionEffectType.JUMP)) {
			tags.add("jump");
			moveSpeed += (jump * .06);
		}

		if (moveSpeed > 0.046
				&& moveSpeed < 0.047
				&& Utils.format(deltaY, 4) == 0.0784) {
			tags.add("fall");
			moveSpeed += 1;
		}

		if (data.movement.deltaV == 0) data.timers.horizontalIdle.reset();

		if (data.timers.horizontalIdle.wasReset() && data.timers.horizontalIdle.getResetStreak() == 0) {
			tags.add("idle");
			moveSpeed += 0.5;
		}

		if (data.enviorment.inWeb.wasReset() && data.enviorment.inWeb.getResetStreak() >= 1) {
			tags.add("web");
			moveSpeed -= 0.2;
		}

		if (data.enviorment.onSoulSand.hasNotPassed(2)) {
			moveSpeed -= 0.05;
			if (type == Material.ICE || type == Material.PACKED_ICE) {
				moveSpeed -= 0.1;
				tags.add("soulice");
			} else tags.add("soul");
		}

		if (data.enviorment.onSlime.hasNotPassed(10)) {
			tags.add("slime");
			moveSpeed -= 0.07;
		}

		double dyf = Utils.format(data.movement.deltaV, 4);
		if (dyf > -0.0785 && dyf < 0) {
			tags.add("first");
			moveSpeed += 0.21;
		}

		double horizontalMove = (horizontalDistance - previousHorizontal) - moveSpeed;
		if (horizontalDistance > 0.1) {
			debug("+%.2f,tags=%s", horizontalMove, Utils.join(tags.stream(), ","));

			if (horizontalMove > 0) {
				if (fail("+%.2f,v=%.2f,tags=%s", horizontalMove, data.velocity.deltaH, Utils.join(tags.stream(), ","))) setback();
			} else {
				if (getPacketOffset() < 5) {
					if (data.locations.size() > 3) {
						Pair<TimedLocation, Double> current = data.locations.get(data.locations.size() - 1);
						Pair<TimedLocation, Double> latter = data.locations.get(data.locations.size() - 2);

						double angle = Math.abs(Utils.getAngle(current.getX().toLocation(player.getWorld()), latter.getX().toLocation(player.getWorld())));
						if (angle < 100 && data.movement.deltaH > data.movement.lastDeltaH) {
							double omniSprintMove = horizontalMove + (moveSpeed * 0.6);
							omniSprintMove -= data.velocity.deltaH;
							if (data.enviorment.inLiquid.hasNotPassed(5)) omniSprintMove *= 0.9;
							//find(OmniSprint.class).debug("Angle: %.2f,%.2f", angle, omniSprintMove);
							if (omniSprintMove > 0) {
								if (omniVl++ >= 2) {
									if (find(OmniSprint.class).fail("%.2f,ang=%.2f,tags=%s", omniSprintMove, angle, Utils.join(tags.stream(), ",")))
										setback();
								}
							} else {
								omniVl = 0;
							}
						}

						if (player.isBlocking()) noSlowStreak++;
						else noSlowStreak = 0;

						if (noSlowStreak > 4) {
							double slowMove = horizontalMove + (moveSpeed * 0.6);
							slowMove -= data.velocity.deltaH;
							if (slowMove > 0) {
								if (find(NoSlow.class).fail("%.2f,tags=%s", slowMove * 100, Utils.join(tags.stream(), ",")))
									setback();
							}
						}

						if (data.timers.lastSneak.wasReset() && data.timers.lastSneak.getResetStreak() > 3) {
							double slowMove = horizontalMove + (moveSpeed * 0.6);
							slowMove -= data.velocity.deltaH;
							if (slowMove > 0) {
								if (find(SneakSpeed.class).fail("%.2f,tags=%s", slowMove * 100, Utils.join(tags.stream(), ",")))
									setback();
							}
						}
					}
				}
			}
		}

		this.previousDistance = horizontalDistance * drag;
		this.drag = getBlockFriction();
	}
}
