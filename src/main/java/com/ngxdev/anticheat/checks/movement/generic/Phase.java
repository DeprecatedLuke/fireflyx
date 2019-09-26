package com.ngxdev.anticheat.checks.movement.generic;

import com.ngxdev.anticheat.api.check.Check;
import api.CheckType;
import com.ngxdev.anticheat.utils.Helper;
import com.ngxdev.anticheat.utils.Pair;
import com.ngxdev.anticheat.utils.world.BlockData;
import com.ngxdev.anticheat.utils.world.types.RayCollision;
import com.ngxdev.anticheat.utils.world.types.SimpleCollisionBox;
import com.ngxdev.tinyprotocol.packet.in.WrappedInFlyingPacket;
import com.ngxdev.anticheat.utils.Materials;
import com.ngxdev.anticheat.utils.packet.WrappedPacketPlayOutWorldParticles;
import org.bukkit.block.Block;
import org.bukkit.util.Vector;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

import static api.CheckType.State.BETA;
import static api.CheckType.Type.GAMEPLAY;

@CheckType(id = "phase", name = "Phase", type = GAMEPLAY, state = BETA, alert = false)
public class Phase extends Check {

    void check(WrappedInFlyingPacket packet) {
        if (!packet.isPos()
                || !canCheckMovement()
                || data.state.isTeleporing) return;

        if (data.movement.deltaV > -0.0981 && data.movement.deltaV < -0.0979) return;

        SimpleCollisionBox currentHitbox = Helper.getMovementHitbox(player);
        SimpleCollisionBox newHitbox = Helper.getMovementHitbox(player, packet.getX(), packet.getY(), packet.getZ());
        currentHitbox.expand(-0.0625); newHitbox.expand(-0.0625); // reduce falseflag chances
        SimpleCollisionBox wrapped = Helper.wrap(currentHitbox, newHitbox);

        List<Block> all = Helper.getBlocks(data.enviorment.handler, wrapped);
        List<Block> currentBlocks = Helper.blockCollisions(all, currentHitbox);
        List<Block> newBlocks = Helper.blockCollisions(all, newHitbox);

        for (Block b : newBlocks) {
            if (!currentBlocks.contains(b)) {
                if (Materials.checkFlag(b.getType(), Materials.SOLID) && !Materials.checkFlag(b.getType(), Materials.STAIRS)) {
                    fail("t=%s", b.getType().name());
                    if (isDebug())
                        BlockData.getData(b.getType()).getBox(b, data.protocolVersion)
                                .draw(WrappedPacketPlayOutWorldParticles.EnumParticle.FLAME, data.singleton);
                    setback();
                    return;
                }
            }
        }

        if (!currentHitbox.isCollided(newHitbox)) { // moved too far, must check between
            List<SimpleCollisionBox> downcasted = new LinkedList<>();
            Helper.toCollisions(all.stream().filter(b -> Materials.checkFlag(b.getType(), Materials.SOLID)).collect(Collectors.toList())).forEach((b) -> b.downCast(downcasted));
            Vector newPos = new Vector(packet.getX(), packet.getY()+player.getEyeHeight(), packet.getZ());
            // so this is something stupid, Just some dumb ray tracing to check if anything was between,
            // its a bad patch for phasing and i should replace it later, but for now...
            Vector oldPos = player.getEyeLocation().toVector();
            double dist = newPos.distance(oldPos);
            Vector rayDir = newPos.subtract(oldPos);
            RayCollision ray = new RayCollision(oldPos, rayDir);
            Pair<Double, Double> pair = new Pair<>(0d, 0d);
            for (SimpleCollisionBox box : downcasted) {
                if (RayCollision.intersect(ray, box, pair)) {
                    if (pair.getX() <= dist) {
                        if (data.timers.slimePush.wasReset() && dist >= 1) {
                            fail("d=%.2f", dist);
                        }
                        setback();
                    }
                }
            }
        }
    }

}
