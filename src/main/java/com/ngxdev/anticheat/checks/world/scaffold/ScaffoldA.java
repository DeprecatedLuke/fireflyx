package com.ngxdev.anticheat.checks.world.scaffold;

import com.ngxdev.anticheat.api.check.Check;
import api.CheckType;
import com.ngxdev.anticheat.data.TimedLocation;
import com.ngxdev.tinyprotocol.packet.in.WrappedInBlockPlacePacket;
import com.ngxdev.tinyprotocol.packet.types.BaseBlockPosition;

import static api.CheckType.Type.WORLD;

@CheckType(id = "scaffold:a", name = "Scaffold A", type = WORLD)
public class ScaffoldA extends Check {
    private BaseBlockPosition lastBlock;
    private float lastYaw;
    private float lastPitch;
    private float lastX;
    private float lastY;
    private float lastZ;
    private double vl;

    void check(WrappedInBlockPlacePacket packet) {
        if (!canCheck()) return;
        if (data.locations.isEmpty()) return;
        BaseBlockPosition blockPosition = packet.getPosition();
        float x = packet.getVecX();
        float y = packet.getVecY();
        float z = packet.getVecZ();
        if (this.lastBlock != null && (blockPosition.getX() != this.lastBlock.getX() || blockPosition.getY() != this.lastBlock.getY() || blockPosition.getZ() != this.lastBlock.getZ())) {
            TimedLocation location = data.locations.getLast().getX();
            if (this.lastX == x && this.lastY == y && this.lastZ == z) {
                final float deltaAngle = Math.abs(this.lastYaw - location.getYaw()) + Math.abs(this.lastPitch - location.getPitch());
                if (deltaAngle > 4.0f && ++vl >= 4.0) {
                    fail("x=%.1f,y=%.1f,z=%.1f,d=%.1f,d=%.1f", x, y, z, deltaAngle, vl);
                }
            } else {
                vl -= 0.5;
            }
            this.lastX = x;
            this.lastY = y;
            this.lastZ = z;
            this.lastYaw = location.getYaw();
            this.lastPitch = location.getPitch();
        }
        this.lastBlock = blockPosition;
    }
}
