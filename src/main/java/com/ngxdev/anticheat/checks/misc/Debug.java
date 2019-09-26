package com.ngxdev.anticheat.checks.misc;

import com.ngxdev.anticheat.api.check.Check;
import api.CheckType;
import com.ngxdev.anticheat.checks.combat.Reach;
import com.ngxdev.anticheat.utils.Helper;
import com.ngxdev.anticheat.utils.Pair;
import com.ngxdev.anticheat.utils.world.BlockData;
import com.ngxdev.anticheat.utils.world.CollisionBox;
import com.ngxdev.anticheat.utils.world.types.RayCollision;
import com.ngxdev.anticheat.utils.world.types.SimpleCollisionBox;
import com.ngxdev.tinyprotocol.api.ProtocolVersion;
import com.ngxdev.anticheat.utils.packet.WrappedPacketPlayOutWorldParticles;
import net.minecraft.server.v1_8_R3.PacketPlayOutEntityVelocity;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.util.Vector;

import java.util.LinkedList;
import java.util.concurrent.ThreadLocalRandom;

import static api.CheckType.State.EXPERIMENTAL;
import static api.CheckType.Type.DEBUG;

@CheckType(id = "debug:tools", name = "debug tools", type = DEBUG, state = EXPERIMENTAL)
public class Debug extends Check implements Listener {

    @EventHandler(priority = EventPriority.LOWEST)
    public void debug(PlayerInteractEvent e) {
        if (!isDebug()) return;

        Block block = e.getClickedBlock();
        if (e.getItem() == null) return;

        if (e.getItem().getType() == Material.ARROW) {
            // uh don't use this on non 1.8 servers then k thx.
            double x = ThreadLocalRandom.current().nextDouble() / 2 + 0.2;
            double z = ThreadLocalRandom.current().nextDouble() / 2 + 0.2;
            if (ThreadLocalRandom.current().nextBoolean()) x = -x;
            if (ThreadLocalRandom.current().nextBoolean()) z = -z;
            sendPacket(new PacketPlayOutEntityVelocity(player.getEntityId(), x, +0.39, z));
        }

        if (e.getAction() != Action.RIGHT_CLICK_BLOCK) return;

        if (e.getItem().getType() == Material.STICK) {
            CollisionBox box;
            if (e.getPlayer().isSneaking())
                box = BlockData.getData(block.getType()).getBox(block, data.protocolVersion);
            else box = BlockData.getData(block.getType()).getBox(block, ProtocolVersion.getGameVersion());
            box.draw(WrappedPacketPlayOutWorldParticles.EnumParticle.FLAME, data.singleton);
            e.setCancelled(true);
        } else if (e.getItem().getType() == Material.BLAZE_ROD) {
            e.setCancelled(true);
            CollisionBox box;
            LinkedList<SimpleCollisionBox> downcasted = new LinkedList<>();
            if (e.getPlayer().isSneaking())
                box = BlockData.getData(block.getType()).getBox(block, data.protocolVersion);
            else box = BlockData.getData(block.getType()).getBox(block, ProtocolVersion.getGameVersion());
//            box.draw(WrappedPacketPlayOutWorldParticles.EnumParticle.FLAME,data.singleton);
            box.downCast(downcasted);
            Location eye = player.getEyeLocation();
            Vector v = eye.getDirection();
            RayCollision ray = new RayCollision(eye.getX(), eye.getY(), eye.getZ(), v.getX(), v.getY(), v.getZ());

            Pair<Double, Double> pair = new Pair<>(999d, 999d);
            SimpleCollisionBox closest = null;
            double dist = pair.getX();

            for (SimpleCollisionBox b : downcasted) {
                if (RayCollision.intersect(ray, b, pair)) {
                    double dist2 = pair.getX();
                    if (dist2 <= dist) {
                        closest = b;
                        dist = dist2;
                    }
                }
            }
            if (closest != null) closest.draw(WrappedPacketPlayOutWorldParticles.EnumParticle.FLAME, data.singleton);
            else player.sendMessage("Collision Miss");
        } else if (e.getItem().getType() == Material.BLAZE_POWDER) {
            e.setCancelled(true);
            CollisionBox box;
            LinkedList<SimpleCollisionBox> downcasted = new LinkedList<>();
            box = BlockData.getData(block.getType()).getBox(block, data.protocolVersion);
            box.downCast(downcasted);
            Bukkit.getOnlinePlayers().stream().filter(p -> !p.equals(e.getPlayer())).forEach(p -> downcasted.add(Reach.getHitbox(p.getLocation())));
            Location eye = player.getEyeLocation();
            Vector v = eye.getDirection();
            RayCollision ray = new RayCollision(eye.getX(), eye.getY(), eye.getZ(), v.getX(), v.getY(), v.getZ());
            Pair<Double, Double> pair = new Pair<>(999d, 999d);
            SimpleCollisionBox closest = null;
            double dist = pair.getX();
            for (SimpleCollisionBox b : downcasted) {
                if (RayCollision.intersect(ray, b, pair)) {
                    double dist2 = pair.getX();
                    if (dist2 <= dist) {
                        closest = b;
                        dist = dist2;
                    }
                }
            }
            v = ray.collisionPoint(closest);
            if (v == null) return;
            Helper.drawPoint(v, WrappedPacketPlayOutWorldParticles.EnumParticle.FLAME, data.singleton);
        } else if (e.getItem().getType() == Material.BOOK) {
            Helper.getMovementHitbox(player).draw(WrappedPacketPlayOutWorldParticles.EnumParticle.FLAME, data.singleton);
        }
    }
}
