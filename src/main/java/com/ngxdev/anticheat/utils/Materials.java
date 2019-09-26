package com.ngxdev.anticheat.utils;

import org.bukkit.Material;

public class Materials {
    private static final int[] MATERIAL_FLAGS = new int[256];

    public static final int SOLID  = 0b00000000000000000000000000001;
    public static final int LADDER = 0b00000000000000000000000000010;
    public static final int WALL   = 0b00000000000000000000000000100;
    public static final int STAIRS = 0b00000000000000000000000001000;
    public static final int SLABS  = 0b00000000000000000000000010000;
    public static final int WATER  = 0b00000000000000000000000100000;
    public static final int LAVA   = 0b00000000000000000000001000000;
    public static final int LIQUID = 0b00000000000000000000001100000;
    public static final int ICE    = 0b00000000000000000000010000000;
    public static final int FENCE  = 0b00000000000000000000100000000;

    static {
        for (int i = 0; i < MATERIAL_FLAGS.length; i++) {
            Material material = Material.values()[i];

            if (material.isSolid()) {
                MATERIAL_FLAGS[i] |= SOLID;
            }

            if (material.name().endsWith("_STAIRS")) {
                MATERIAL_FLAGS[i] |= STAIRS;
            }

            if (material.name().contains("SLAB")) {
                MATERIAL_FLAGS[i] |= SLABS;
            }
        }

        // fix some types where isSolid() returns the wrong value
        MATERIAL_FLAGS[Material.SIGN_POST.getId()] = 0;
        MATERIAL_FLAGS[Material.WALL_SIGN.getId()] = 0;
        MATERIAL_FLAGS[Material.GOLD_PLATE.getId()] = 0;
        MATERIAL_FLAGS[Material.IRON_PLATE.getId()] = 0;
        MATERIAL_FLAGS[Material.WOOD_PLATE.getId()] = 0;
        MATERIAL_FLAGS[Material.STONE_PLATE.getId()] = 0;
        MATERIAL_FLAGS[165] = SOLID;
        MATERIAL_FLAGS[Material.DIODE_BLOCK_OFF.getId()] = SOLID;
        MATERIAL_FLAGS[Material.DIODE_BLOCK_ON.getId()] = SOLID;
        MATERIAL_FLAGS[Material.CARPET.getId()] = SOLID;
        MATERIAL_FLAGS[Material.SNOW.getId()] = SOLID;
        MATERIAL_FLAGS[Material.ANVIL.getId()] = SOLID;
        MATERIAL_FLAGS[Material.WATER_LILY.getId()] = SOLID;
        MATERIAL_FLAGS[Material.SKULL.getId()] = SOLID;

        // liquids
        MATERIAL_FLAGS[Material.WATER.getId()] |= LIQUID | WATER;
        MATERIAL_FLAGS[Material.STATIONARY_WATER.getId()] |= LIQUID | WATER;
        MATERIAL_FLAGS[Material.LAVA.getId()] |= LIQUID | LAVA;
        MATERIAL_FLAGS[Material.STATIONARY_LAVA.getId()] |= LIQUID | LAVA;

        // ladders
        MATERIAL_FLAGS[Material.LADDER.getId()] |= LADDER | SOLID;
        MATERIAL_FLAGS[Material.VINE.getId()] |= LADDER | SOLID;

        // walls
        MATERIAL_FLAGS[Material.FENCE.getId()] |= WALL;
        MATERIAL_FLAGS[Material.FENCE_GATE.getId()] |= WALL;
        MATERIAL_FLAGS[Material.COBBLE_WALL.getId()] |= WALL;
        MATERIAL_FLAGS[Material.NETHER_FENCE.getId()] |= WALL;

        // slabs
        MATERIAL_FLAGS[Material.BED_BLOCK.getId()] |= SLABS;

        // ice
        MATERIAL_FLAGS[Material.ICE.getId()] |= ICE;
        MATERIAL_FLAGS[Material.PACKED_ICE.getId()] |= ICE;

        for (Material mat : Material.values()) {
        	if (mat.name().contains("FENCE")) MATERIAL_FLAGS[mat.getId()] |= FENCE;
	    }
    }

    private Materials() {
    }

    public static boolean checkFlag(Material material, int flag) {
        return (MATERIAL_FLAGS[material.getId()] & flag) == flag;
    }

}
