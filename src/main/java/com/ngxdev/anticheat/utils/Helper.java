package com.ngxdev.anticheat.utils;

import com.ngxdev.anticheat.handler.TinyProtocolHandler;
import com.ngxdev.anticheat.handler.handler.PlayerSizeHandler;
import com.ngxdev.anticheat.utils.world.BlockData;
import com.ngxdev.anticheat.utils.world.CollisionBox;
import com.ngxdev.anticheat.utils.world.CollisionHandler;
import com.ngxdev.anticheat.utils.world.types.RayCollision;
import com.ngxdev.anticheat.utils.world.types.SimpleCollisionBox;
import com.ngxdev.tinyprotocol.api.ProtocolVersion;
import com.ngxdev.anticheat.utils.packet.WrappedPacket;
import com.ngxdev.anticheat.utils.packet.WrappedPacketPlayOutWorldParticles;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

import static com.ngxdev.anticheat.utils.packet.WrappedPacket.c;

public class Helper {

	private static Class<?> entity = c("net.minecraft.server." + WrappedPacket.VERSION + ".Entity");
	private static Class<?> craftEntity = c("org.bukkit.craftbukkit." + WrappedPacket.VERSION + ".entity.CraftEntity");

	private static Field craftEntityHandle = Reflective.access(craftEntity, "entity");
	private static Field lastX = Reflective.access(entity, "lastX");
	private static Field lastY = Reflective.access(entity, "lastY");
	private static Field lastZ = Reflective.access(entity, "lastZ");
	private static Field lastYaw = Reflective.access(entity, "lastYaw");
	private static Field lastPitch = Reflective.access(entity, "lastPitch");


	public static List<Block> getBlocksNearby(CollisionHandler handler, SimpleCollisionBox collisionBox) {
		try {
			return handler.getBlocks().stream().filter(b -> b.getType() != Material.AIR
					&& BlockData.getData(b.getType()).getBox(b, handler.getData().protocolVersion)
					.isCollided(collisionBox))
					.collect(Collectors.toList());
		} catch (NullPointerException e) {
			return new ArrayList<>();
		}
	}

	public static List<Block> getBlocksNearby2(World world, SimpleCollisionBox collisionBox, int mask) {
		int x1 = (int) Math.floor(collisionBox.xMin);
		int y1 = (int) Math.floor(collisionBox.yMin);
		int z1 = (int) Math.floor(collisionBox.zMin);
		int x2 = (int) Math.ceil(collisionBox.xMax);
		int y2 = (int) Math.ceil(collisionBox.yMax);
		int z2 = (int) Math.ceil(collisionBox.zMax);
		List<Block> blocks = new LinkedList<>();
		Block block;
		for (int x = x1; x <= x2; x++)
			for (int y = y1; y <= y2; y++)
				for (int z = z1; z <= z2; z++)
					if ((block = getBlockAt(world, x, y, z)) != null
							&& block.getType()!=Material.AIR)
						if (Materials.checkFlag(block.getType(),mask))
							blocks.add(block);
		return blocks;
	}

	public static List<Block> getBlocksNearby(CollisionHandler handler, SimpleCollisionBox collisionBox, int mask) {
		return handler.getBlocks().stream().filter(b -> b.getType() != Material.AIR
				&& Materials.checkFlag(b.getType(), mask)
				&& BlockData.getData(b.getType()).getBox(b, handler.getData().protocolVersion)
				.isCollided(collisionBox))
				.collect(Collectors.toList());
	}

	public static List<Block> getBlocks(CollisionHandler handler, SimpleCollisionBox collisionBox) {
		return Helper.blockCollisions(getBlocksNearby(handler, collisionBox), collisionBox);
	}

	public static List<Block> getBlocks(CollisionHandler handler, SimpleCollisionBox collisionBox, int material) {
		return Helper.blockCollisions(getBlocksNearby(handler, collisionBox), collisionBox, material);
	}

	public static SimpleCollisionBox getMovementHitbox(Player player, double x, double y, double z) {
		return PlayerSizeHandler.instance.bounds(player, x, y, z);
	}

	public static SimpleCollisionBox getMovementHitbox(Player player) {
		return PlayerSizeHandler.instance.bounds(player);
	}

	public static SimpleCollisionBox getCombatHitbox(Player player, ProtocolVersion version) {
		if (version.isBelow(ProtocolVersion.V1_9))
			return PlayerSizeHandler.instance.bounds(player).expand(.1, 0, .1);
		return PlayerSizeHandler.instance.bounds(player);
	}

	private static Block getBlockAt(World world, int x, int y, int z) {
		if (world.isChunkLoaded(x >> 4, z >> 4)) return world.getChunkAt(x >> 4, z >> 4).getBlock(x & 15, y, z & 15);
		return null;
	}

	public static List<CollisionBox> toCollisions(List<Block> blocks) {
		List<CollisionBox> collisions = new LinkedList<>();
		for (Block b : blocks)
			collisions.add(BlockData.getData(b.getType()).getBox(b, ProtocolVersion.getGameVersion()));
		return collisions;
	}

	public static List<SimpleCollisionBox> toCollisionsDowncasted(List<Block> blocks) {
		List<SimpleCollisionBox> collisions = new LinkedList<>();
		for (Block b : blocks)
			BlockData.getData(b.getType()).getBox(b, ProtocolVersion.getGameVersion()).downCast(collisions);
		return collisions;
	}

	public static CollisionBox toCollisions(Block b) {
		return BlockData.getData(b.getType()).getBox(b, ProtocolVersion.getGameVersion());
	}

	public static List<Block> blockCollisions(List<Block> blocks, SimpleCollisionBox box) {
		List<Block> collisions = new LinkedList<>();
		for (Block b : blocks)
			if (BlockData.getData(b.getType()).getBox(b, ProtocolVersion.getGameVersion()).isCollided(box))
				collisions.add(b);
		return collisions;
	}

	public static List<Block> blockCollisions(List<Block> blocks, SimpleCollisionBox box, int material) {
		List<Block> collisions = new LinkedList<>();
		for (Block b : blocks)
			if (Materials.checkFlag(b.getType(), material))
				if (BlockData.getData(b.getType()).getBox(b, ProtocolVersion.getGameVersion()).isCollided(box))
					collisions.add(b);
		return collisions;
	}

	public static <C extends CollisionBox> List<C> collisions(List<C> boxes, CollisionBox box) {
		List<C> collisions = new LinkedList<>();
		for (CollisionBox b : boxes)
			if (b.isCollided(box))
				collisions.add((C) b);
		return collisions;
	}

	public static double nearest(List<SimpleCollisionBox> boxes, RayCollision ray) {
		Pair<Double, Double> pair = new Pair<>();
		double nearest = -1;
		for (SimpleCollisionBox b : boxes)
			if (RayCollision.intersect(ray, b, pair)) {
				if (nearest == -1 || nearest > pair.getX())
					nearest = pair.getX();
			}
		return nearest;
	}

	public static double distance(SimpleCollisionBox box, RayCollision ray) {
		Pair<Double, Double> pair = new Pair<>();
		if (RayCollision.intersect(ray, box, pair))
			return pair.getX();
		return -1;
	}

	public static int angularDistance(double alpha, double beta) {
		while (alpha < 0) alpha += 360;
		while (beta < 0) beta += 360;
		double phi = Math.abs(beta - alpha) % 360;
		return (int) (phi > 180 ? 360 - phi : phi);
	}

	public static Vector vector(double yaw, double pitch) {
		Vector vector = new Vector();
		vector.setY(-Math.sin(Math.toRadians(pitch)));
		double xz = Math.cos(Math.toRadians(pitch));
		vector.setX(-xz * Math.sin(Math.toRadians(yaw)));
		vector.setZ(xz * Math.cos(Math.toRadians(yaw)));
		return vector;
	}

	public static List<SimpleCollisionBox> collisionsDowncasted(List<CollisionBox> boxes, SimpleCollisionBox box) {
		List<SimpleCollisionBox> collisions = new LinkedList<>();
		List<SimpleCollisionBox> targets = new LinkedList<>();
		for (CollisionBox b : boxes) {
			b.downCast(targets);
		}
		for (SimpleCollisionBox b : targets)
			if (b.isCollided(box))
				collisions.add(b);
		return collisions;
	}

	public static Location getLastLocation(Entity entity) {
		Location location = entity.getLocation();
		try {
			Object handle = craftEntityHandle.get(entity);
			location.setX((double) lastX.get(handle));
			location.setY((double) lastY.get(handle));
			location.setZ((double) lastZ.get(handle));
			location.setYaw((float) lastYaw.get(handle));
			location.setPitch((float) lastPitch.get(handle));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return location;
	}

	public static Location getLastEyeLocation(LivingEntity entity) {
		return getLastLocation(entity).add(0, entity.getEyeHeight(), 0);
	}

	public static SimpleCollisionBox wrap(SimpleCollisionBox a, SimpleCollisionBox b) {
		double minX = a.xMin < b.xMin ? a.xMin : b.xMin;
		double minY = a.yMin < b.yMin ? a.yMin : b.yMin;
		double minZ = a.zMin < b.zMin ? a.zMin : b.zMin;
		double maxX = a.xMax > b.xMax ? a.xMax : b.xMax;
		double maxY = a.yMax > b.yMax ? a.yMax : b.yMax;
		double maxZ = a.zMax > b.zMax ? a.zMax : b.zMax;
		return new SimpleCollisionBox(minX, minY, minZ, maxX, maxY, maxZ);
	}

	public static SimpleCollisionBox wrap(List<SimpleCollisionBox> box) {
		if (!box.isEmpty()) {
			SimpleCollisionBox wrap = box.get(0).copy();
			for (int i = 1; i < box.size(); i++) {
				SimpleCollisionBox a = box.get(i);
				if (wrap.xMin > a.xMin) wrap.xMin = a.xMin;
				if (wrap.yMin > a.yMin) wrap.yMin = a.yMin;
				if (wrap.zMin > a.zMin) wrap.zMin = a.zMin;
				if (wrap.xMax < a.xMax) wrap.xMax = a.xMax;
				if (wrap.yMax < a.yMax) wrap.yMax = a.yMax;
				if (wrap.zMax < a.zMax) wrap.zMax = a.zMax;
			}
			return wrap;
		}
		return null;
	}

	public static void drawCuboid(SimpleCollisionBox box, WrappedPacketPlayOutWorldParticles.EnumParticle particle, Collection<? extends Player> players) {
		Step.GenericStepper<Float> x = Step.step((float) box.xMin, 0.241f, (float) box.xMax);
		Step.GenericStepper<Float> y = Step.step((float) box.yMin, 0.241f, (float) box.yMax);
		Step.GenericStepper<Float> z = Step.step((float) box.zMin, 0.241f, (float) box.zMax);
		for (float fx : x) {
			for (float fy : y) {
				for (float fz : z) {
					int check = 0;
					if (x.first() || x.last()) check++;
					if (y.first() || y.last()) check++;
					if (z.first() || z.last()) check++;
					if (check >= 2) {
						Object packet = new WrappedPacketPlayOutWorldParticles(particle, true, fx, fy, fz,
								0F, 0F, 0F, 0, 0).getPacket();
						for (Player p : players) TinyProtocolHandler.sendPacket(p, packet);
					}
				}
			}
		}
	}

	public static void drawPoint(Vector point, WrappedPacketPlayOutWorldParticles.EnumParticle particle, Collection<? extends Player> players) {
		Object packet = new WrappedPacketPlayOutWorldParticles(particle, true, (float) point.getX(), (float) point.getY(), (float) point.getZ(),
				0F, 0F, 0F, 0, 0).getPacket();
		for (Player p : players) TinyProtocolHandler.sendPacket(p, packet);
	}

	public static void drawRay(RayCollision collision, WrappedPacketPlayOutWorldParticles.EnumParticle particle, Collection<? extends Player> players) {
		for (double i = 0; i < 8; i += 0.2) {
			float fx = (float) (collision.originX + (collision.directionX * i));
			float fy = (float) (collision.originY + (collision.directionY * i));
			float fz = (float) (collision.originZ + (collision.directionZ * i));
			Object packet = new WrappedPacketPlayOutWorldParticles(particle, true, fx, fy, fz,
					0F, 0F, 0F, 0, 0).getPacket();
			for (Player p : players) TinyProtocolHandler.sendPacket(p, packet);
		}
	}

}
