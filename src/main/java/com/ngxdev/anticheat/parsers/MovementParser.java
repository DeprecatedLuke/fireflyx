package com.ngxdev.anticheat.parsers;

import api.CheckWrapper;
import com.ngxdev.anticheat.api.check.Check;
import com.ngxdev.anticheat.api.check.Priority;
import com.ngxdev.anticheat.api.check.type.Parser;
import com.ngxdev.anticheat.api.events.PlayerTakeVelocityEvent;
import com.ngxdev.anticheat.checks.movement.experimental.NoVelocity;
import com.ngxdev.anticheat.data.SimpleLocation;
import com.ngxdev.anticheat.data.TimedLocation;
import com.ngxdev.anticheat.data.playerdata.PlayerData;
import com.ngxdev.anticheat.utils.Helper;
import com.ngxdev.anticheat.utils.world.CollisionHandler;
import com.ngxdev.anticheat.utils.world.Material2;
import com.ngxdev.anticheat.utils.world.types.SimpleCollisionBox;
import com.ngxdev.tinyprotocol.api.ProtocolVersion;
import com.ngxdev.tinyprotocol.packet.in.WrappedInFlyingPacket;
import com.ngxdev.tinyprotocol.packet.out.WrappedOutVelocityPacket;
import com.ngxdev.tinyprotocol.reflection.FieldAccessor;
import com.ngxdev.tinyprotocol.reflection.Reflection;
import com.ngxdev.anticheat.utils.Materials;
import com.ngxdev.anticheat.utils.MathUtils;
import com.ngxdev.anticheat.utils.evicting.EvictingList;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.event.player.PlayerTeleportEvent;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

@Parser
public class MovementParser extends Check {
	private FieldAccessor<Boolean> fieldCheckMovement = Reflection.getField("{nms}.PlayerConnection", "checkMovement", boolean.class);
	private FieldAccessor<Boolean> fieldJustTeleported = Reflection.getField("{nms}.PlayerConnection", "justTeleported", boolean.class);
	private LinkedList<SimpleLocation> velocities = new EvictingList<>(10);
	private int velSent;
	private int velReceived;
	private WrappedInFlyingPacket lastFlying;

	@Override
	public void init(PlayerData data, CheckWrapper wrapper) {
		super.init(data, wrapper);
	}

	@Priority(-5)
	void parse(WrappedInFlyingPacket packet) {
		data.lag.packetDelay = (int) (System.currentTimeMillis() - data.lag.lastPacket);
		data.lag.lastPacket = System.currentTimeMillis();
		boolean checkMovement = fieldCheckMovement.get(data.playerConnection);
		if (checkMovement) data.movementTicks++;
		else data.movementTicks = 0;

		if (packet.isGround()) data.enviorment.onGroundClient.reset();
		long offset = 50 - data.lag.packetDelay;
		data.lag.currentTime += data.lag.packetDelay;
		data.lag.differencial += offset;
		data.timers.lastPacket.reset();

		if (data.protocolVersion.isAbove(ProtocolVersion.V1_9)) {
			if (data.lag.packetDelay < 40) {
				data.lag.skips += 2;
				data.lag.skips = Math.max(data.lag.skips, 5);
			}
		} else {
			if (data.lag.packetDelay > 75) {
				data.lag.skips += (data.lag.packetDelay / 50) * 2;
				data.lag.skips = Math.max(data.lag.skips, 5);
			}
		}

		data.lag.skips = Math.min(data.lag.skips, 40);

		if (data.lag.playerTime == -1L) data.lag.playerTime = System.currentTimeMillis() - 50;
		else data.lag.playerTime += 50;

		data.state.isTeleporing = data.movementTicks <= 0;
		if (!data.state.isTeleporing) {
			if (packet.isPos()) {
				data.state.isSettingback = false;
				data.currentTick++;

				data.movement.fx = data.movement.tx;
				data.movement.fy = data.movement.ty;
				data.movement.fz = data.movement.tz;

				data.movement.tx = packet.getX();
				data.movement.ty = packet.getY();
				data.movement.tz = packet.getZ();

				data.movement.lastDeltaH = data.movement.deltaH;
				data.movement.lastDeltaV = data.movement.deltaV;
				data.movement.deltaH = Math.hypot(data.movement.tx - data.movement.fx, data.movement.tz - data.movement.fz);
				data.movement.deltaV = data.movement.ty - data.movement.fy;

				if (data.movement.hasJumped) {
					data.movement.hasJumped = false;
					data.movement.inAir = true;
				}
				if (packet.isGround()) data.movement.inAir = false;
				parseEnvironment();

				if (data.movement.deltaV > 0.4199999 || (data.enviorment.inWeb.wasReset() && data.movement.deltaV > 0.0209)) {
					data.timers.lastJump.reset();
					data.movement.hasJumped = true;
				}

				if (data.state.isTakingVelocity) {
					if (data.enviorment.blockAbove.wasReset()) {
						velReceived = 0;
						velSent = 0;
					}

					SimpleLocation matched = velocities.stream().filter(loc -> loc.getY() < 0 || Math.abs(loc.getY() - Math.abs(data.movement.ty - data.movement.fy)) < 0.01).findFirst().orElse(null);
					if (matched != null) {
						if (matched.getY() < 0) data.timers.lastWankVelocity.reset();
						velocities.remove(matched);
						data.state.isTakingVelocity = false;
						double x = matched.getX();
						double y = matched.getY();
						double z = matched.getZ();
						parseVelocity(Math.hypot(x, z), y);
					} else if (data.timers.lastVelocitySent.hasPassed(data.getEstimatedPacketLag() + 1)) {
						parseVelocity(data.movement.deltaH, data.movement.deltaV);
					}
				}
				if (data.lag.skips > 0) data.lag.skips--;
			}

			if (packet.isLook()) {
				data.movement.fyaw = data.movement.tyaw;
				data.movement.fpitch = data.movement.tpitch;
				data.movement.tyaw = packet.getYaw();
				data.movement.tpitch = packet.getPitch();


				float deltaYaw = MathUtils.getDistanceBetweenAngles(data.movement.tyaw, data.movement.fyaw);
				float deltaPitch = MathUtils.getDistanceBetweenAngles(data.movement.tpitch, data.movement.fpitch);
				data.movement.yawDifference = Math.abs(deltaYaw - data.movement.deltaYaw);
				data.movement.pitchDifference = Math.abs(deltaPitch - data.movement.deltaPitch);
				data.movement.lastDeltaYaw = data.movement.deltaYaw;
				data.movement.lastDeltaPitch = data.movement.deltaPitch;
				data.movement.deltaYaw = deltaYaw;
				data.movement.deltaPitch = deltaPitch;
			}
		}
	}

	void parseVelocity(double deltaH, double deltaV) {
		data.velocity.deltaH = Math.max(deltaH, data.movement.deltaH);
		data.velocity.deltaV = Math.max(deltaV, data.movement.deltaV);

		data.state.isTakingVelocity = false;
		data.timers.lastVelocity.reset();
		fireChecks(new PlayerTakeVelocityEvent(deltaH, deltaV));

		if (velSent - velReceived > 0) velReceived += 2;
		else velReceived++;
	}

	void parse(WrappedOutVelocityPacket packet) {
		data.state.isTakingVelocity = true;
		data.timers.lastVelocitySent.reset();
		velocities.add(new TimedLocation(packet.getX(), packet.getY(), packet.getZ()));
		velSent++;

		if (packet.getY() < 0 || data.enviorment.inLiquid.hasNotPassed(5)) {
			velReceived = 0;
			velSent = 0;
		} else {
			if (velSent - velReceived == 4) {
				find(NoVelocity.class).fail();
				velReceived = 0;
				velSent = 0;
			}
		}
	}

	void parse(PlayerRespawnEvent event) {
		moveTo(event.getRespawnLocation());
	}

	void parse(PlayerTeleportEvent event) {
		if (!event.isCancelled()) {
			moveTo(event.getTo());
		}
	}

	void moveTo(Location loc) {
		velReceived = 0;
		velSent = 0;
		data.movementTicks = 0;

		data.lag.packetDelay = 50;

		data.movement.inAir = false;
		data.movement.hasJumped = false;
		data.timers.lastTeleport.reset();
		data.state.isTeleporing = false;
		data.state.isInventoryOpen = false;
		data.state.isPlacing = false;
		data.state.isDigging = false;

		data.movement.fx = loc.getX();
		data.movement.fy = loc.getY();
		data.movement.fz = loc.getZ();
		data.movement.tx = loc.getX();
		data.movement.ty = loc.getY();
		data.movement.tz = loc.getZ();
		data.movement.fyaw = loc.getYaw();
		data.movement.fpitch = loc.getPitch();
		data.movement.tyaw = loc.getYaw();
		data.movement.tpitch = loc.getPitch();
		data.movement.lastDeltaH = 0;
		data.movement.lastDeltaV = 0;
		data.movement.deltaH = 0;
		data.movement.deltaV = 0;

		parseEnvironment();
	}

	private void parseEnvironment() {
		if (data.movement.deltaH == 0 && data.movement.deltaV == 0) return;
		World world = player.getWorld();

		int startX = Location.locToBlock(data.movement.tx - 0.3 - data.movement.deltaH);
		int endX = Location.locToBlock(data.movement.tx + 0.3 + data.movement.deltaH);
		int startY = Location.locToBlock(data.movement.ty - 0.51 + data.movement.deltaV);
		int endY = Location.locToBlock(data.movement.ty + 1.99 + data.movement.deltaV);
		int startZ = Location.locToBlock(data.movement.tz - 0.3 - data.movement.deltaH);
		int endZ = Location.locToBlock(data.movement.tz + 0.3 + data.movement.deltaH);

		List<Block> blocks = new ArrayList<>();

		int it = 9 * 9;
		start:
		for (int chunkx = startX >> 4; chunkx <= endX >> 4; ++chunkx) {
			int cx = chunkx << 4;

			for (int chunkz = startZ >> 4; chunkz <= endZ >> 4; ++chunkz) {
				if (!world.isChunkLoaded(chunkx, chunkz)) {
					data.timers.inUnloadedChunks.reset();
					continue;
				}
				Chunk chunk = world.getChunkAt(chunkx, chunkz);
				if (chunk != null) {
					int cz = chunkz << 4;
					int xstart = startX < cx ? cx : startX;
					int xend = endX < cx + 16 ? endX : cx + 16;
					int zstart = startZ < cz ? cz : startZ;
					int zend = endZ < cz + 16 ? endZ : cz + 16;

					for (int x = xstart; x <= xend; ++x) {
						for (int z = zstart; z <= zend; ++z) {
							for (int y = startY < 0 ? 0 : startY; y <= endY; ++y) {
								if (it-- <= 0) {
									break start;
								}
								Block block = chunk.getBlock(x & 15, y, z & 15);
								if (block.getType() != Material.AIR) {
									blocks.add(block);
								}
							}
						}
					}
				}
			}
		}


		List<Entity> entities;

		if (data.movement.deltaH < 1 && data.movement.deltaV < 1) entities =player.getNearbyEntities(1 + data.movement.deltaH, 2 + data.movement.deltaV, 1 + data.movement.deltaH);
		else entities = Collections.emptyList();

		CollisionHandler handler = new CollisionHandler(blocks, entities, data);

		handler.setSize(0.6, 0.5);
		handler.setOffset(-0.49);
		if (handler.isCollidedWith(Materials.SOLID) || handler.contains(EntityType.BOAT))
			data.enviorment.onGround.reset();
		else data.enviorment.inAir.reset();

		if (handler.isCollidedWith(Materials.STAIRS | Materials.SLABS | Materials.FENCE))
			data.enviorment.onWeirdBlock.reset();

		handler.setSingle(true);
		if (handler.isCollidedWith(Materials.ICE)) data.enviorment.onIce.reset();
		if (handler.isCollidedWith(Material.SOUL_SAND)) data.enviorment.onSoulSand.reset();
		if (handler.isCollidedWith(Material2.SLIME_BLOCK)) data.enviorment.onSlime.reset();
		handler.setSingle(false);
		handler.setOffset(0);

		handler.setSize(0.6, 1.8);
		boolean lava = handler.isCollidedWith(Materials.LAVA);
		boolean water = handler.isCollidedWith(Materials.WATER);
		if (lava) data.enviorment.inLava.reset();
		if (water) data.enviorment.inWater.reset();
		if (lava || water) {
			data.enviorment.inLiquid.reset();
		}

		if (handler.isCollidedWith(Material.WEB)) data.enviorment.inWeb.reset();

		handler.setSize(2.0, 0.0);
		handler.setSingle(true);
		if (handler.isCollidedWith(Materials.LADDER)) data.enviorment.onLadder.reset();
		handler.setSingle(false);


		handler.setSize(0.6, 2.01);
		if (handler.isCollidedWith(Materials.SOLID)) data.enviorment.blockAbove.reset();

		SimpleCollisionBox box = Helper.getMovementHitbox(player);
		box.expand(Math.abs(data.movement.fx - data.movement.tx) + 0.1, -0.1, Math.abs(data.movement.fz - data.movement.tz) + 0.1);
		if (!Helper.blockCollisions(handler.getBlocks(), box).isEmpty()) data.enviorment.collidedHorizontally.reset();
		box = Helper.getMovementHitbox(player);
		box.expand(0, 0.1, 0);
		if (!Helper.blockCollisions(handler.getBlocks(), box).isEmpty()) data.enviorment.collidedVertically.reset();

		data.enviorment.lastHandler = data.enviorment.handler;
		data.enviorment.handler = handler;
	}
}
