package com.ngxdev.anticheat.checks.movement.speed;

import com.ngxdev.anticheat.api.check.Check;
import api.CheckType;
import com.ngxdev.anticheat.api.check.Priority;
import com.ngxdev.anticheat.utils.Helper;
import com.ngxdev.anticheat.utils.world.CollisionBox;
import com.ngxdev.anticheat.utils.world.types.SimpleCollisionBox;
import com.ngxdev.tinyprotocol.packet.in.WrappedInFlyingPacket;
import com.ngxdev.anticheat.utils.Materials;
import org.bukkit.block.Block;
import org.bukkit.entity.EntityType;
import org.bukkit.potion.PotionEffectType;

import java.util.List;

import static api.CheckType.Type.MOVEMENT;

@CheckType(id = "speed:v", name = "Speed V", type = MOVEMENT)
public class SpeedV extends Check {
    @Priority(-2)
    void check(WrappedInFlyingPacket packet) {
        double dy = data.movement.deltaV;
        if (!packet.isPos()
                || !isMoving()
                || !canCheckMovement()
                || data.enviorment.handler.contains(EntityType.BOAT)
                || data.timers.slimePush.hasNotPassed(10)
                || isGliding()) return;

        SimpleCollisionBox box = Helper.getMovementHitbox(player);
        box.expand(Math.abs(data.movement.fx - data.movement.tx) - 0.1, -0.1, Math.abs(data.movement.fz - data.movement.tz) - 0.1);
        List<Block> blocks = Helper.getBlocksNearby(data.enviorment.handler, box, Materials.SOLID);
        List<CollisionBox> boxes_ = Helper.toCollisions(blocks);
        List<SimpleCollisionBox> boxes = Helper.collisionsDowncasted(boxes_, box);

        double minStep = 0;
        for (SimpleCollisionBox collision : boxes) {
            minStep = Math.max(collision.yMax - player.getLocation().getY(), minStep);
            debug("+%.1f", collision.yMax - player.getLocation().getY());
        }


        double limit = (data.timers.slimePush.hasNotPassed(5) ? 1 : (minStep <= 0.5 ? 0.51 : 0.43)) + (.11 * getPotionEffectLevel(PotionEffectType.JUMP)) + Math.abs(data.movement.lastDeltaV);
        if (dy > limit) {
            if (data.enviorment.onSlime.hasNotPassed(2)) return;
            if (fail("+%.1f,+%.1f", Math.abs(dy - 0.5), Math.abs(minStep - dy))) setback();
        }
    }
}
