package com.ngxdev.anticheat.checks.badpackets;

import com.ngxdev.anticheat.api.check.Check;
import api.CheckType;
import com.ngxdev.tinyprotocol.packet.in.WrappedInUseEntityPacket;
import com.ngxdev.tinyprotocol.packet.types.Vec3D;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import static api.CheckType.Type.BADPACKET;
import static com.ngxdev.tinyprotocol.packet.in.WrappedInUseEntityPacket.EnumEntityUseAction.INTERACT_AT;

@CheckType(id = "badpackets:i", name = "BadPackets I", type = BADPACKET)
public class BadPacketsI extends Check {
    void check(WrappedInUseEntityPacket packet) {
        if (!canCheck()) return;
        if (packet.getAction() == INTERACT_AT) {
            Entity entity = packet.getEntity();
            if (entity instanceof Player) {
                Vec3D vec3D = packet.getVector();
                if ((Math.abs(vec3D.a) > 0.41 || Math.abs(vec3D.b) > 1.91 || Math.abs(vec3D.c) > 0.41)) {
                    if (fail()) packet.setCancelled(true);
                }
            }
        }
    }
}
