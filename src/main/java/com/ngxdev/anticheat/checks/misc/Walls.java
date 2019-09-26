package com.ngxdev.anticheat.checks.misc;

import com.ngxdev.anticheat.api.check.Check;
import com.ngxdev.anticheat.api.check.Setting;
import api.CheckType;
import com.ngxdev.anticheat.utils.Helper;
import com.ngxdev.anticheat.utils.world.BlockData;
import com.ngxdev.anticheat.utils.world.CollisionBox;
import com.ngxdev.anticheat.utils.world.types.RayCollision;
import com.ngxdev.anticheat.utils.world.types.SimpleCollisionBox;
import com.ngxdev.tinyprotocol.api.ProtocolVersion;
import com.ngxdev.anticheat.utils.Materials;
import com.ngxdev.anticheat.utils.packet.WrappedPacketPlayOutWorldParticles;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import static api.CheckType.Type.GAMEPLAY;
import static com.ngxdev.anticheat.utils.Utils.EIGHTH;

//@CheckType(id = "misc:walls", name = "Walls", type = GAMEPLAY, alert = false)
public class Walls extends Check implements Listener {

	@Setting
	public static double distanceThreshold = 0;
	@Setting
	public static double cameraThreshold = 20;

	void check(PlayerInteractEvent e) {
		if (e.getAction() != Action.RIGHT_CLICK_BLOCK && e.getAction() != Action.LEFT_CLICK_BLOCK)
			return;

		Block block = e.getClickedBlock();
		Material type = block.getType();
		CollisionBox blockbox = Helper.toCollisions(block);
		CollisionBox playerbox = Helper.getMovementHitbox(player);

		if (blockbox.isNull())
			blockbox = BlockData.getData(Material.STONE).getBox(block, ProtocolVersion.UNKNOWN); // shrug

		List<SimpleCollisionBox> boxes = new LinkedList<>();
		blockbox.downCast(boxes);
		playerbox.downCast(boxes);

		SimpleCollisionBox bounding = Helper.wrap(boxes);
		if (bounding == null) return;
		List<Block> blocks = Helper.getBlocks(data.enviorment.handler, bounding, Materials.SOLID);
		blocks.remove(block);

		List<SimpleCollisionBox> targetBlock = new LinkedList<>();
		blockbox.downCast(targetBlock);
		// idk why i keep it a list, but its fine.
		targetBlock = Collections.singletonList(Helper.wrap(targetBlock));

		if (type == Material.SNOW) {
			targetBlock.forEach(s -> s.yMax += EIGHTH);
		}


		List<SimpleCollisionBox> collisions = Helper.toCollisionsDowncasted(blocks);
		RayCollision ray = new RayCollision(data.movement.tEyePos(), data.movement.tDir());

		double targetDistance = Helper.nearest(targetBlock, ray);
		double distance = Helper.nearest(collisions, ray);

		boolean shouldFlag = false;
		if (Helper.angularDistance(data.movement.tyaw, data.movement.fyaw) < cameraThreshold
				&& Helper.angularDistance(data.movement.tpitch, data.movement.fpitch) < cameraThreshold)
			shouldFlag = true;

		if (targetDistance == -1) {
			ray = new RayCollision(data.movement.fEyePos(), data.movement.fDir());
			targetDistance = Helper.nearest(targetBlock, ray);
			distance = Helper.nearest(collisions, ray);
		}

		if (targetDistance == -1) {
			if (shouldFlag) fail("A,t=" + block.getType()); // fail("Wasn't looking at block: " + block.getType());
			if (isDebug()) blockbox.draw(WrappedPacketPlayOutWorldParticles.EnumParticle.FLAME, data.singleton);
			e.setCancelled(true);
			return;
		}

		if (distance != -1 && (distance + distanceThreshold) < targetDistance) {
			if (shouldFlag) fail("B"); // fail("View of block was obstructed, yet still tried to interact");
			e.setCancelled(true);
			return;
		}
	}
}
