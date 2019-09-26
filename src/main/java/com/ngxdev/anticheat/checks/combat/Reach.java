package com.ngxdev.anticheat.checks.combat;

import api.CheckType;
import api.ConfigValueX;
import com.ngxdev.anticheat.api.check.Check;
import com.ngxdev.anticheat.api.check.Priority;
import com.ngxdev.anticheat.api.check.Setting;
import com.ngxdev.anticheat.data.SimpleLocation;
import com.ngxdev.anticheat.data.TimedLocation;
import com.ngxdev.anticheat.data.playerdata.PlayerData;
import com.ngxdev.anticheat.utils.Pair;
import com.ngxdev.anticheat.utils.world.types.RayCollision;
import com.ngxdev.anticheat.utils.world.types.SimpleCollisionBox;
import com.ngxdev.tinyprotocol.packet.in.WrappedInArmAnimationPacket;
import com.ngxdev.tinyprotocol.packet.in.WrappedInFlyingPacket;
import com.ngxdev.tinyprotocol.packet.in.WrappedInUseEntityPacket;
import org.bukkit.Location;
import org.bukkit.event.Listener;

import java.util.List;
import java.util.stream.Collectors;

import static api.CheckType.State.BETA;
import static api.CheckType.Type.COMBAT;
import static com.ngxdev.tinyprotocol.packet.in.WrappedInUseEntityPacket.EnumEntityUseAction.ATTACK;

@CheckType(id = "reach:a", name = "Reach", type = COMBAT, state = BETA)
public class Reach extends Check implements Listener {
	@Setting
	static double banReach = ConfigValueX.REACH_BAN.asDouble();
	@Setting
	static double cancelReach = ConfigValueX.REACH_CANCEL.asDouble();
	@Setting
	static double reachNerf = 2.9;
	@Setting
	static double trustLimit = 0.25;
	@Setting
	static int pingSensitivity = 150;
	@Setting
	static int streakSensitivity = 2;

	double reachStreak = 0;
	boolean cancel = false;

	PlayerData target;

	//the lower the trust, the less reach leniency we give the player, till it's effectively 0.
	double trust = 1;

	void check(WrappedInArmAnimationPacket packet) {
		reachStreak = Math.max(0, reachStreak - 0.1);
		trust = Math.min(1, trust + 0.005);
	}

	@Priority(50)
	void check(WrappedInFlyingPacket packet) {
		if (!canCheck() || !canCheckMovement()) return;
		if (data.locations.size() != 1)
			data.locations.removeIf(pair -> System.currentTimeMillis() - pair.getX().getTime() > 1000);

		synchronized (data.locations) {
			data.locations.add(new Pair<>(new TimedLocation(data), data.movement.deltaH + Math.abs(data.movement.deltaV)));
		}
		//getHitbox(player, locations.getLast()).draw(WrappedPacketPlayOutWorldParticles.EnumParticle.CRIT, Bukkit.getOnlinePlayers().stream().filter(p -> !p.equalsLenient(player)).collect(Collectors.toList()));
		//locations.forEach(loc -> getHitbox(player, loc)
		if (target != null) {
			synchronized (target.locations) {
				Location eyeLocation = new SimpleLocation(data).toLocation(player.getWorld()).add(0, 1.53, 0);
				RayCollision ray = new RayCollision(eyeLocation.toVector(), eyeLocation.getDirection());

				List<TimedLocation> locations = target.getEstimatedLocations(data.lag.currentTime, pingSensitivity + data.lag.differencial);
				List<Pair<SimpleCollisionBox, Double>> boxes = target.locations.stream()
						.filter(pair -> locations.contains(pair.getX()))
						.map(pair -> new Pair<>(getHitbox(pair.getX()), pair.getY()))
						.collect(Collectors.toList());

				//ray.draw(WrappedPacketPlayOutWorldParticles.EnumParticle.CRIT, Bukkit.getOnlinePlayers());


				double lowest = 0;
				int collided = 0;
				for (Pair<SimpleCollisionBox, Double> pair : boxes) {
					Pair<Double, Double> collision = new Pair<>();
					if (RayCollision.intersect(ray, pair.getX(), collision)) {
						//Helper.drawPoint(ray.collisionPoint(pair.getX()), WrappedPacketPlayOutWorldParticles.EnumParticle.FLAME, Bukkit.getOnlinePlayers());
						double reach = collision.getX() - 0.0625;
						reach -= ((pair.getY() * 2) + data.movement.deltaH); //todo make this better
						lowest = lowest == 0 ? reach : Math.min(lowest, reach);
						collided++;
					}
					//box.draw(WrappedPacketPlayOutWorldParticles.EnumParticle.CRIT, Bukkit.getOnlinePlayers());
				}
				if (lowest > 3) {
					reachStreak = Math.min(streakSensitivity + 2, reachStreak + 1);
					if (reachStreak >= streakSensitivity) {
						if (lowest > banReach)
							fail("r=%.3f,b=%d,l=%d,c=%d", lowest, boxes.size(), locations.size(), collided);
						if (lowest > cancelReach) trust -= 0.5;
					}
				} else {
					reachStreak = Math.max(0, reachStreak - 0.1);
					trust = Math.min(1, trust + 0.005);
				}
				if (lowest > reachNerf && trust < trustLimit) {
					cancel = true;
				}

				target = null;
			}
		}
	}

	void check(WrappedInUseEntityPacket packet) {
		if (packet.getAction() != ATTACK && target != null) return;

		target = PlayerData.get(packet.getId());
		if (cancel) {
			cancel = false;
			packet.setCancelled(true);
		}
	}

	public static SimpleCollisionBox getHitbox(SimpleLocation l) {
		return new SimpleCollisionBox().offset(l.getX(), l.getY(), l.getZ()).expand(.4, 0, .4)
				.expandMax(0, 1.85, 0);
	}

	public static SimpleCollisionBox getHitbox(Location l) {
		return new SimpleCollisionBox().offset(l.getX(), l.getY(), l.getZ()).expand(.4, 0, .4)
				.expandMax(0, 1.85, 0);
	}
}
