package com.ngxdev.anticheat.utils;

import com.ngxdev.anticheat.utils.world.types.SimpleCollisionBox;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.bukkit.util.Vector;

@SuppressWarnings("WeakerAccess")
public class AngleMagic {


    @SuppressWarnings("ConstantConditions") // corners aways returns 8 vectors, minyaw can only be null if it returns 0. it will never do that
    public static AngularRange calculate(SimpleCollisionBox box, Vector point) {
        Vector[] corners = box.corners(); // returns Vector[8] of each corner
        Float minYaw = null, minPitch = null, maxYaw = null, maxPitch = null;
        for (Vector v : corners) {
            Pair<Float, Float> pair = calculate(point,v);
            if (minYaw == null || minYaw > pair.getY()) minYaw = pair.getY();
            if (maxYaw == null || maxYaw < pair.getY()) maxYaw = pair.getY();
            if (minPitch == null || minPitch > pair.getX()) minPitch = pair.getX();
            if (maxPitch == null || maxPitch < pair.getX()) maxPitch = pair.getX();
        }
        return new AngularRange(minYaw,maxYaw,minPitch,maxPitch);
    }

    // returns a pair, y being YAW, x being PITCH
    public static Pair<Float, Float> convert(Vector point) {
        Pair<Float, Float> pair = new Pair<>();
        pair.setY((float) Math.atan2(point.getZ(), point.getY()));
        pair.setX((float) (Math.atan2(Math.sqrt(point.getZ() * point.getZ() + point.getX() * point.getX()), point.getY()) + Math.PI));
        return pair;
    }

    public static Pair<Float, Float> calculate(Vector point, Vector target) {
        return convert(point.clone().subtract(target));
    }











    @AllArgsConstructor @Data public static class AngularRange {
        private float minYaw,maxYaw,minPitch,maxPitch;

        public float distYaw(float yaw) {
            float f = Helper.angularDistance(yaw, minYaw);
            yaw = Helper.angularDistance(yaw,maxYaw);
            return yaw < f ? yaw : f;
        }
        public float distPitch(float pitch) {
            float f = Helper.angularDistance(pitch,minPitch);
            pitch = Helper.angularDistance(pitch,maxPitch);
            return !(pitch < f) ? f : pitch;
        }

        @Override
        public String toString() {
            return "{ " +
                      "mnY=" + minYaw +
                    ", mxY=" + maxYaw +
                    ", mnP=" + minPitch +
                    ", mxP=" + maxPitch + " }";
        }
    }
}
